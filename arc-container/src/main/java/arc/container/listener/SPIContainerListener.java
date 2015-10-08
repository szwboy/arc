package arc.container.listener;

import arc.components.support.DependencyInjector;
import arc.container.event.ContainerListener;
import arc.container.event.ContainerStartedEvent;
import arc.container.spi.SPIDependencyFactory;
import arc.core.spi.DependencyFactory;
import arc.core.spi.ServiceLoader;

public class SPIContainerListener implements ContainerListener<ContainerStartedEvent> {

	public SPIContainerListener(DependencyInjector injector){
		this.injector= injector;
	}
	
	@Override
	public void onContainerEvent(ContainerStartedEvent event) {
		DependencyFactory dependencyFactory= ServiceLoader.getLoader(DependencyFactory.class).getProvider("spi");
		if(dependencyFactory instanceof SPIDependencyFactory){
			((SPIDependencyFactory)dependencyFactory).setInjector(injector);
		}
	}
	
	private DependencyInjector injector;

}
