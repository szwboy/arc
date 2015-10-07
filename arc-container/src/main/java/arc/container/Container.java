package arc.container;

import java.util.Set;
import arc.components.factory.ComponentFactory;
import arc.components.factory.RegistrableComponentFactory;
import arc.components.xml.ComponentReader;
import arc.components.xml.XmlComponentReader;


/**
 * 
 * @author sunzhongwei
 *
 */
public class Container implements ComponentFactory{
	private boolean created;
	private RegistrableComponentFactory componentFactory;
	
	void create(String locations){
		if(created) throw new IllegalStateException("container is in creation");
		created= true;
		
		synchronized(this){
			componentFactory= new RegistrableComponentFactory();
			ComponentReader reader= new XmlComponentReader(componentFactory);
			reader.loadDefinition(locations.split(",;"));
		}
	}
	
	public ComponentFactory getComponentFactory(){
		return componentFactory;
	}

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

	@Override
	public <T> T inject(Class<T> type) {
		return componentFactory.inject(type);
	}

}
