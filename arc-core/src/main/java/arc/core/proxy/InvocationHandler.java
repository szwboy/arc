package arc.core.proxy;

import java.lang.reflect.Method;

public interface InvocationHandler {

	Object invoke(Method method, Object[] args, Object proxy);
}
