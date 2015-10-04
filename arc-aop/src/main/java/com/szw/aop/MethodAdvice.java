package com.szw.aop;



public interface MethodAdvice extends Advice {
	
	String[] getRefArgs();
	
	Object[] getBindingArgs();
	
	void bindingArgs(Object[] args);
	
}
