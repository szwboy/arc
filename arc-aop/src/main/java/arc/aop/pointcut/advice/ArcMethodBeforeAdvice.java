package com.arc.aop.advice;

import java.lang.reflect.Method;

import com.arc.aop.pointcut.Pointcut;

public class ArcMethodBeforeAdvice implements MethodBeforeAdvice {

	@Override
	public void before(Method m, Object[] args, Object target) {
		// TODO Auto-generated method stub

	}
	
	public ArcMethodBeforeAdvice(Method m, Pointcut pc){
		
	}

}
