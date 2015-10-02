package arc.common.proxy;

import arc.annotation.annotation.Adaptive;
import arc.annotation.annotation.Spi;


@Spi("javassist")
public interface ProxyFactory {

	@Adaptive
	<T>T getProxy(Class<T> ifc);
}
