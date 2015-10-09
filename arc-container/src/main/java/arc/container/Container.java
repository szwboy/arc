package arc.container;

import java.util.Set;

import arc.components.factory.ComponentFactory;
import arc.components.factory.RegistrableComponentFactory;
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
import arc.container.event.listener.SPIContainerListener;

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
		this.locations= locations;
		if(refresh){
			refresh();
		}
	}
	
	private void registerListeners(){
		eventMulticaster.addListener(new SPIContainerListener(componentFactory));
		Set<String> listenerNames= componentFactory.getComponentNames(ContainerListener.class);
		for(String listenerName: listenerNames){
			eventMulticaster.addListenerComponentName(listenerName);
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
				reader.loadDefinition(locations.split(",;"));
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
	public <T> T getComponent(Class<T> type) {
		return componentFactory.getComponent(type);
	}

	@Override
	public <T> Set<String> getComponentNames(Class<T> type) {
		return componentFactory.getComponentNames(type);
	}
	
	public void publishEvent(ContainerEvent eve) {
		
		eventMulticaster.multicastEvent(eve);
	}
	
	public void refresh(){
		create(locations);
		initContainerEventMulticaster();
		registerListeners();
		publishEvent(new ContainerRefreshedEvent(this));
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
