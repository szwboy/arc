package com.szw.aop;

import java.lang.reflect.Method;

public interface MethodBeforeAdvice extends MethodAdvice {

	public void before(Method method,Object[] args,Object target) throws Throwable;

}
