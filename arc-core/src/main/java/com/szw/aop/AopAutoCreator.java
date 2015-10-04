package com.szw.aop;

import java.util.List;

import com.szw.ioc.Container;
import com.szw.ioc.ContainerPostProcessor;
import com.szw.ioc.ExtendedContainer;
import com.szw.ioc.Inject;

public class AopAutoCreator implements ContainerPostProcessor{

	@Inject("container")
	private Container container;
	@Override
	public Object postProcess(Object o,Container container) {
		
		AopContextCreator creator=new AopContextCreatorImpl(o);
		container.inject(creator);

		List<Advisor> advisors=container.getInstance(Advisor.class);
		if(advisors!=null&&advisors.size()>0){
			o=creator.createProxyFactory(advisors).getProxy();
		}
		
		return o;
	}

	@Override
	public int getOrdered() {
		return CONTAINERPOSTPROCESSOR_ORDER;
	}

	@Override
	public Object postProcessor() {
		if(container instanceof ExtendedContainer) ((ExtendedContainer)container).addContainerPostProcessor(this);
		return this;
	}

}
