package arc.core.spi;


public class SimpleDependencyFactory implements DependencyFactory{

	@Override
	public <T> T depend(Class<T> type) {
		try {
			return type.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}


}
