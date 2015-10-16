package arc.container.event.listener;

import arc.components.factory.annotation.Qualifier;
import arc.components.support.DependencyInjector;
import arc.container.event.ContainerEvent;
import arc.container.event.ContainerListener;
import arc.container.spi.SPIDependencyFactory;
import arc.core.spi.DependencyFactory;
import arc.core.spi.DependencyFactoryAdaptive;
import arc.core.spi.ServiceLoader;

public class SPIContainerListener implements ContainerListener<ContainerEvent> {
	
	@Override
	public void onContainerEvent(ContainerEvent event) {
		//init the spi factory and set it the default factory
		DependencyFactory dependencyFactory= ServiceLoader.getLoader(DependencyFactory.class).getProvider("spi");
		if(dependencyFactory instanceof SPIDependencyFactory){
			((SPIDependencyFactory)dependencyFactory).setInjector(injector);
		}
		
		DependencyFactory adaptivedependencyFactory= ServiceLoader.getLoader(DependencyFactory.class).getAdaptiveProvider();
		if(adaptivedependencyFactory instanceof DependencyFactoryAdaptive){
			((DependencyFactoryAdaptive)dependencyFactory).setDependencyFactory(adaptivedependencyFactory);
		}
	}
	
	@Qualifier("componentFactory")
	private DependencyInjector injector;

}
