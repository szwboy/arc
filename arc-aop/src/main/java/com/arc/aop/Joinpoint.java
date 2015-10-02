package com.arc.aop;

import java.lang.reflect.AccessibleObject;

public interface Joinpoint {

	Object proceed();
	
	Object getThis();
	
	AccessibleObject getStaticPart();
}
