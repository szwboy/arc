package com.szw.aop;

import com.szw.Ordered;

public interface Advisor extends Ordered{
	
	Pointcut getPointcut();
	
	Advice getAdvice();
}
