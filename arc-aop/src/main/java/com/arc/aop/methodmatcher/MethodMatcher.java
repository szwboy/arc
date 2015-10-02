package com.arc.aop.methodmatcher;

import java.lang.reflect.Method;

public interface MethodMatcher {

	boolean matcher(Method method, Object[] args, Class<?> c);
}
