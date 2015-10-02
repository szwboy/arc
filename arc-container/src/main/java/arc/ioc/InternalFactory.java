package arc.ioc;

import arc.annotation.annotation.Adaptive;
import arc.annotation.annotation.Spi;

@Spi
public interface InternalFactory<T> {

	@Adaptive(number=1, expr="simpleName")
	T getObject(Class<T> type, String name);
	
	T getObject(String name);
}
