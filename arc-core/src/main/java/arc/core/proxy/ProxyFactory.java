package arc.core.proxy;

import arc.core.spi.annotation.Adaptive;
import arc.core.spi.annotation.Spi;



@Spi("javassist")
public interface ProxyFactory {

	@Adaptive
	<T>T getProxy(Class<T> ifc);
}
