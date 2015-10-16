package arc.core.spi;

import arc.core.spi.annotation.Adaptive;

@Adaptive
public class DependencyFactoryAdaptive implements DependencyFactory{

	private DependencyFactory dependencyFactory;
	
	@Override
	public <T> T depend(Class<T> type) {
		try {
			if(dependencyFactory== null)
				return type.newInstance();
			else return dependencyFactory.depend(type);
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public void setDependencyFactory(DependencyFactory dependencyFactory) {
		this.dependencyFactory = dependencyFactory;
	}

}
