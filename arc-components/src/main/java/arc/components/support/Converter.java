package arc.components.support;

import arc.core.spi.annotation.Adaptive;
import arc.core.spi.annotation.Spi;


@Spi
public interface Converter {

	/*
	 * arg1 represents string to be converted
	 * arg2 represents the type which the value will be converted to
	 */
	@Adaptive(expr="simpleName",number=2)
	<T>T convert(String value, Class<T> c);
}
