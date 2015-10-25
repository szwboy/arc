package arc.aop;

import java.lang.reflect.Method;

public interface MethodMatcher {

	boolean matches(Method method, Class<?> clz);
	
	boolean matches(Method method, Class<?> clz, Object[] args);
	
	MethodMatcher TRUE= TrueMethodMatcher.TRUE;
}
