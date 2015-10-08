package arc.container;

import java.util.Set;

import arc.components.factory.ComponentFactory;
import arc.components.factory.RegistrableComponentFactory;
import arc.components.support.DependencyInjector;
import arc.components.xml.ComponentReader;
import arc.components.xml.XmlComponentReader;
import arc.container.event.ContaienrEventMulticaster;
import arc.container.event.ContainerEvent;
import arc.container.event.ContainerEventPublisher;
import arc.container.event.ContainerListener;
import arc.container.event.ContainerStartedEvent;
import arc.container.event.SimpleContainerEventMulticaster;
import arc.container.listener.SPIContainerListener;

/**
 * 
 * @author sunzhongwei
 *
 */
public class Container implements ComponentFactory, ContainerEventPublisher{
	private boolean created;
	private RegistrableComponentFactory componentFactory;
	private ContaienrEventMulticaster eventMulticaster;
	
	public Container(String locations){
		create(locations);
	}
	
	public void init(){
		initContainerEventMulticaster();
		registerListeners();
	}
	
	private void registerListeners(){
		eventMulticaster.addListener(new SPIContainerListener(componentFactory));
		Set<String> listenerNames= componentFactory.getComponentNames(ContainerListener.class);
	}
	
	public void start(){
		
		publishEvent(new ContainerStartedEvent(this));
	}
	
	private void initContainerEventMulticaster(){
		this.eventMulticaster= new SimpleContainerEventMulticaster(componentFactory);
	}
	
	void create(String locations){
		if(created) throw new IllegalStateException("container is in creation");
		created= true;
		
		synchronized(componentFactory){
			if(componentFactory== null){
				componentFactory= new RegistrableComponentFactory();
				ComponentReader reader= new XmlComponentReader(componentFactory);
				reader.loadDefinition(locations.split(",;"));
			}
		}
	}
	
	protected ComponentFactory getComponentFactory(){
		return componentFactory;
	}
	
	/**=============================================
	 * implementation of {@link ComponentFactory}
	 * =============================================*/
	@Override
	public <T> T getComponent(String name, Class<T> type) {
		return componentFactory.getComponent(name, type);
	}

	@Override
	public <T> T getComponent(Class<T> type) {
		return componentFactory.getComponent(type);
	}

	@Override
	public <T> Set<String> getComponentNames(Class<T> type) {
		return componentFactory.getComponentNames(type);
	}
	
	protected void finished(){
		
	}
	
	public void publishEvent(ContainerEvent eve) {
		
		eventMulticaster.multicastEvent(eve);
	}

}
