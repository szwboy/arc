package com.szw.aop;

import com.szw.ClassUtils;
import com.szw.StringUtils;

public class AssistIntroductionAdvice implements IntroductionAdvice {

	private Class<?> ifc;
	private Class<?> impl;

	public AssistIntroductionAdvice(String ifc,String impl) {
		this.ifc = ClassUtils.forName(ifc);
		if(!StringUtils.isBalank(impl))
			this.impl = ClassUtils.forName(impl);
	}

	public Class<?> getIfc() {
		return ifc;
	}

	public Class<?> getImpl() {
		return impl;
	}
	
	public boolean equals(Object o){
		if(!(o instanceof AssistIntroductionAdvice)){
			return false;
		}
		
		if(this==o) return true;
		
		AssistIntroductionAdvice advice=((AssistIntroductionAdvice)o);
		if(advice.getIfc()==ifc&&advice.getImpl()==impl) return true;
		
		return false;
	}
	
	public int hashCode(){
		return ifc.hashCode()*16+(impl==null?0:impl.hashCode());
	}
	
}
