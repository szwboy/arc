package arc.container.spi;

import arc.components.support.DependencyInjector;
import arc.core.spi.DependencyFactory;

public class SPIDependencyFactory implements DependencyFactory{

	public SPIDependencyFactory(DependencyInjector injector){
		this.injector= injector;
	}
	
	public SPIDependencyFactory(){};
	
	@Override
	public <T> T depend(Class<T> type) {
		return injector.inject(type);
	}
	
	public void setInjector(DependencyInjector injector) {
		this.injector = injector;
	}

	private DependencyInjector injector;

}
