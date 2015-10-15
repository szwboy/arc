package arc.container.event.listener;

import arc.components.factory.annotation.Qualifier;
import arc.components.support.DependencyInjector;
import arc.container.event.ContainerEvent;
import arc.container.event.ContainerListener;
import arc.container.spi.SPIDependencyFactory;
import arc.core.spi.DependencyFactory;
import arc.core.spi.ServiceLoader;

public class SPIContainerListener implements ContainerListener<ContainerEvent> {
	
	@Override
	public void onContainerEvent(ContainerEvent event) {
		DependencyFactory dependencyFactory= ServiceLoader.getLoader(DependencyFactory.class).getProvider("spi");
		if(dependencyFactory instanceof SPIDependencyFactory){
			((SPIDependencyFactory)dependencyFactory).setInjector(injector);
		}
	}
	
	@Qualifier("componentFactory")
	private DependencyInjector injector;

}
