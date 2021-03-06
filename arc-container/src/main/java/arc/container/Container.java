package arc.container;

import java.util.Set;

import arc.components.factory.AbstractComponentFactory;
import arc.components.factory.ComponentFactory;
import arc.components.factory.ComponentPostProcessor;
import arc.components.factory.RegistrableComponentFactory;
import arc.components.support.ComponentRegistry;
import arc.components.support.Scope;
import arc.components.xml.ComponentReader;
import arc.components.xml.XmlComponentReader;
import arc.container.event.ContaienrEventMulticaster;
import arc.container.event.ContainerEvent;
import arc.container.event.ContainerEventPublisher;
import arc.container.event.ContainerListener;
import arc.container.event.ContainerRefreshedEvent;
import arc.container.event.ContainerStartedEvent;
import arc.container.event.ContainerStoppedEvent;
import arc.container.event.SimpleContainerEventMulticaster;

/**
 * 
 * @author sunzhongwei
 *
 */
public class Container implements ComponentFactory, ContainerEventPublisher{
	private boolean running;
	private boolean created;
	private RegistrableComponentFactory componentFactory;
	private ContaienrEventMulticaster eventMulticaster;
	private String locations;
	
	public Container(String locations, boolean refresh){
		this.locations= CONTAINER_EVENT_LOCATION+","+locations;
		if(refresh){
			refresh();
		}
	}
	
	private void registerListeners(){
		Set<String> listenerNames= componentFactory.getComponentNames(ContainerListener.class);
		for(String name: listenerNames){
			eventMulticaster.addListenerComponentName(name);
		}
	}
	
	protected RegistrableComponentFactory getComponentFactory(){
		
		return componentFactory;
	}
	
	private void initContainerEventMulticaster(){
		this.eventMulticaster= new SimpleContainerEventMulticaster(componentFactory);
	}
	
	private void create(String locations){
		if(created) throw new IllegalStateException("container is in creation");
		created= true;
		
		synchronized(this){
			if(componentFactory== null){
				componentFactory= new RegistrableComponentFactory();
				ComponentReader reader= new XmlComponentReader(componentFactory);
				reader.loadDefinition(locations.split(","));
			}
		}
	}
	
	/**=============================================
	 * implementation of {@link ComponentFactory}
	 * =============================================*/
	@Override
	public <T> T getComponent(String name, Class<T> type) {
		return componentFactory.getComponent(name, type);
	}
	
	@Override
	public Object getComponent(String name) {
		return componentFactory.getComponent(name);
	}
	
	public void publishEvent(ContainerEvent eve) {
		
		eventMulticaster.multicastEvent(eve);
	}
	
	public void refresh(){
		synchronized(this){
			create(locations);
			componentFactory.factory("componentFactory", RegistrableComponentFactory.class, componentFactory, Scope.Singleton);
			registerComponentPostProcessors(componentFactory);
			initContainerEventMulticaster();
			registerListeners();
			finishRefreshed();
		}
	}
	
	private void finishRefreshed(){
		publishEvent(new ContainerRefreshedEvent(this));
	}
	
	/**
	 * register post processor for component factory
	 * @param factory
	 */
	private void registerComponentPostProcessors(RegistrableComponentFactory factory){
		Set<String> names= factory.getComponentNames(ComponentPostProcessor.class);
		if(names!= null){
			
			for(String name: names){
				factory.addComponentPostProcessor(factory.getComponent(name, ComponentPostProcessor.class));
			}
		}
	}
	
	public void start(){

		running= true;
		publishEvent(new ContainerStartedEvent(this));
	}
	
	public void stop(){
		destroyComponents();
		running= false;
		publishEvent(new ContainerStoppedEvent(this));
	}
	
	private void destroyComponents(){
		//componentFactory.
	}

}
