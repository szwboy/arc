package com.szw.aop;

import java.lang.reflect.Method;

/**
 * 就是joinpoint的一个特例
 * @author sunzhongwei
 *
 */
public interface MethodInvocation extends Invocation {

	Method getMethod();
}
