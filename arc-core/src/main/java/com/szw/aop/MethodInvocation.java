package com.szw.aop;

import java.lang.reflect.Method;

/**
 * ����joinpoint��һ������
 * @author sunzhongwei
 *
 */
public interface MethodInvocation extends Invocation {

	Method getMethod();
}
