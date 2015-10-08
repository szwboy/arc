package arc.container.spi;

import arc.components.support.DependencyInjector;
import arc.core.spi.DependencyFactory;
import arc.core.spi.annotation.Adaptive;

@Adaptive
public class SPIDependencyFactory implements DependencyFactory{

	public SPIDependencyFactory(DependencyInjector injector){
		this.injector= injector;
	}
	
	@Override
	public <T> T depend(Class<T> type) {
		return injector.inject(type);
	}
	
	public void setInjector(DependencyInjector injector) {
		this.injector = injector;
	}

	private DependencyInjector injector;

}
