package arc.ioc.container;

import arc.components.factory.Container;
import arc.components.factory.RegistrableContainer;

/**
 * 
 * @author sunzhongwei
 *
 */
public class ContainerBuilder{
	private boolean created;
	
	synchronized Container create(boolean loadSingleton){
		if(created) throw new IllegalStateException("container is in creation");
		created= true;
		Container container= new RegistrableContainer();
		
		return container;
	}

}
