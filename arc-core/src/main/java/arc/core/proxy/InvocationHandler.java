package arc.core.proxy;

import java.lang.reflect.Method;

public interface InvocationHandler<T> {

	Object invoke(Method method, Object[] args, Object proxy);
	
	void setObject(T t);
}
