package com.arc.aop;

import arc.common.proxy.Invocation;


public interface Invoker<T> {

	Object invoke(Invocation inv);
}
