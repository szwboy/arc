package com.szw.aop;

import java.util.ArrayList;
import java.util.List;

public class AopContextCreatorImpl implements AopContextCreator {

	private Object t;
	
	public AopContextCreatorImpl(Object t){
		this.t=t;
	}
	
	@Override
	public ProxyFactory createProxyFactory(List<Advisor> advisors) {
		ProxyFactory factory=new ProxyFactoryImpl();
		
		List<Advisor> result=new ArrayList<Advisor>();
		for(Advisor advisor:advisors){
			Pointcut pointcut=advisor.getPointcut();
			
			ClassFilter classFilter=pointcut.getClassFilter();
			if(classFilter.matches(t.getClass())){
				
				Advice advice=advisor.getAdvice();
				if(advice instanceof IntroductionAdvice){
					((ProxyConfig)factory).addIntroductionAdvices(advice);
				}else result.add(advisor);
			}
		}
		
		((ProxyConfig)factory).addAdvisor(result);
		((ProxyConfig)factory).setTarget(t);
		((ProxyConfig)factory).setTargetClass(t.getClass());
		
		return factory;

	}

}
