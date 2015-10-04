package com.arc.aop;

import java.lang.reflect.Method;

import arc.common.proxy.Invocation;

public interface MethodInvocation extends Invocation{

	Method getMethod();
}
