package arc.aop.advice;

import java.lang.reflect.Method;

public interface MethodBeforeAdvice extends BeforeAdvice{

	void before(Method m, Object[] args, Object target);
}
