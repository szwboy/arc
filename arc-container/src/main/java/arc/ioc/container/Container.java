package arc.ioc.container;

import arc.components.factory.ComponentFactory;
import arc.components.factory.RegistrableComponentFactory;


/**
 * 
 * @author sunzhongwei
 *
 */
public class Container{
	private boolean created;
	
	synchronized ComponentFactory create(boolean loadSingleton){
		if(created) throw new IllegalStateException("container is in creation");
		created= true;
		ComponentFactory componentFactory= new RegistrableComponentFactory();
		
		return componentFactory;
	}

}
