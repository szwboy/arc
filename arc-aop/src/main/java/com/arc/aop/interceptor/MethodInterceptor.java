package com.arc.aop.interceptor;

import com.arc.aop.MethodInvocation;

public interface MethodInterceptor {

	Object invoke(MethodInvocation mi);
}
