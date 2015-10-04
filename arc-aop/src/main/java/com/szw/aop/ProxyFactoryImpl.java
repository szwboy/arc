package com.szw.aop;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.szw.assist.Proxy;

public class ProxyFactoryImpl extends ProxyConfig implements ProxyFactory {

	@Override
	public Object getProxy() {
		if(this.getAdvisors().length>0)	
			return Proxy.getProxy(getClasses()).newInstance(this);
		
		return this.getTarget();
	}
	
	private Class<?>[] getClasses(){
		Set<Advice> advices=getIntroductionAdvices();
		List<Class<?>> cls=new ArrayList<Class<?>>();

		if(advices!=null){
			if(advices.size()>0){
				for(Advice advice:advices){
					
					Class<?> impl=((IntroductionAdvice)advice).getImpl();
					if(impl!=null&&!cls.contains(impl)) cls.add(0, impl);
					
					Class<?> ifc=((IntroductionAdvice)advice).getIfc();
					if(!cls.contains(ifc))
						cls.add(ifc);
				}
			}
		}
		
		cls.add(0,getTargetClass());
		
		return cls.toArray(new Class<?>[0]);
	}

}
