package com.szw.aop;

public interface IntroductionAdvice extends Advice {

	Class<?> getIfc();
	
	Class<?> getImpl();
}
