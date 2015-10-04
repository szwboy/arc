package com.szw.aop;

import java.lang.reflect.Method;

public interface MethodMatcher {

	boolean matches(Method method,Class<?> targetClass);
	
	boolean matches(Method method,Class<?> targetClass,Class<?>[] paramTypes);
}
