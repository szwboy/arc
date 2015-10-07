package arc.core.spi;

import arc.core.spi.annotation.Spi;

@Spi
public interface DependencyFactory {
	
	<T>T depend(Class<T> type);
	
}
