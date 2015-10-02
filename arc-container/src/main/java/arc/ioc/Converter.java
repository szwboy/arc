package arc.ioc;

import arc.annotation.annotation.Adaptive;
import arc.annotation.annotation.Spi;

@Spi
public interface Converter {

	/*
	 * arg1 represents string to be converted
	 * arg2 represents the type which the value will be converted to
	 */
	@Adaptive(expr="simpleName",number=2)
	<T>T convert(String value, Class<T> c);
}
