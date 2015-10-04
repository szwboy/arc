package com.szw.aop;

import com.szw.Ordered;

public interface Pointcut extends Ordered{

	ClassFilter getClassFilter();
	
	MethodMatcher getMethodMatcher();
}
