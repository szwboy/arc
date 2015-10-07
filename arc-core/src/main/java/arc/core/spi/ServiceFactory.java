package arc.core.spi;

import arc.core.spi.annotation.Spi;

@Spi
public interface ServiceFactory {

	<T>T getService(String name, Class<T> type);
	
	<T>T inject(T instance);
}
