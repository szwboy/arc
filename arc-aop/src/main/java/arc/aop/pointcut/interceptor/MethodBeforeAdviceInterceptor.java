package com.arc.aop.interceptor;

import com.arc.aop.MethodInvocation;
import com.arc.aop.advice.MethodBeforeAdvice;

public class MethodBeforeAdviceInterceptor implements MethodInterceptor {

	private MethodBeforeAdvice advice;
	
	@Override
	public Object invoke(MethodInvocation mi) {
		return null;
	}

}
