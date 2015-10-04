package com.szw.aop;

import java.util.List;

public interface AopContextCreator {

	ProxyFactory createProxyFactory(List<Advisor> advisors);
}
