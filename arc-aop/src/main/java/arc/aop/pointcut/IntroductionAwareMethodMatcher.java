package arc.aop.pointcut;

import java.lang.reflect.Method;

import arc.aop.MethodMatcher;

public interface IntroductionAwareMethodMatcher extends MethodMatcher {

	boolean matches(Class<?> targetClass, Method m, boolean hasIntroduction);
}
