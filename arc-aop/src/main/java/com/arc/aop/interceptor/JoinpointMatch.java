package com.arc.aop.interceptor;

import com.arc.aop.interceptor.ArcJoinpointMatch.PointcutParameter;

public interface JoinpointMatch {

	PointcutParameter[] getParameterBindings();
	
	void argsBinding();
}
