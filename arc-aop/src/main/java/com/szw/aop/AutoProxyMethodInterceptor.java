package com.szw.aop;

public interface AutoProxyMethodInterceptor extends MethodInterceptor {

	MethodAdvice getMethodAdvice();
}
