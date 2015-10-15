package arc.core.spi;

import arc.core.spi.annotation.Adaptive;
import arc.core.spi.annotation.Spi;

@Spi
public interface DependencyFactory {
	
	@Adaptive
	<T>T depend(Class<T> type);
	
}
