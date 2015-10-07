package arc.core.spi;


public class SimpleServiceFactory implements ServiceFactory{
	
	@Override
	public <T>T getService(String name, Class<T> type) {
		try {
			return type.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new IllegalArgumentException(type.getCanonicalName()+" cannot be instantiated");
		}
	}

	@Override
	public <T> T inject(T instance) {
		return instance;
	}

}
