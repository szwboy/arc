package com.szw.aop;

public class MethodBeforeInterceptor implements AutoProxyMethodInterceptor {
	private MethodBeforeAdvice advice;
	
	public MethodBeforeInterceptor(MethodBeforeAdvice advice){
		this.advice=advice;
	}

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable{
		advice.before(invocation.getMethod(), invocation.getArguments(), invocation.getThis());
		return invocation.proceed();
	}

	@Override
	public MethodAdvice getMethodAdvice() {
		return advice;
	}

}
