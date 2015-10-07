package arc.container;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;

import arc.components.factory.ComponentFactory;
import arc.components.factory.RegistrableComponentFactory;
import arc.components.xml.ComponentReader;
import arc.components.xml.XmlComponentReader;
import arc.container.spi.SPIDependencyFactory;
import arc.core.proxy.ProxyFactory;
import arc.core.spi.DependencyFactory;
import arc.core.spi.ServiceLoader;


/**
 * 
 * @author sunzhongwei
 *
 */
public class Container implements ComponentFactory{
	private boolean created;
	private RegistrableComponentFactory componentFactory;
	
	public Container(String locations){
		create(locations);
	}
	public void start(){
		DependencyFactory dependencyFactory= new SPIDependencyFactory(componentFactory);
		Set<Class<? extends DependencyFactory>> wrapperClasses= ServiceLoader.getLoader(DependencyFactory.class).getWrapperClasses();
		if(wrapperClasses!= null){
			for(Class<? extends DependencyFactory> type: wrapperClasses){
				try {
					type.getConstructor(DependencyFactory.class).newInstance(dependencyFactory);
				} catch (InstantiationException | IllegalAccessException
						| IllegalArgumentException | InvocationTargetException
						| NoSuchMethodException | SecurityException e) {
				}
			}
		}
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
	
	private void publishEvent(){
		
	}

}
