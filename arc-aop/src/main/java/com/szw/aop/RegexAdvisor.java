package com.szw.aop;

import com.szw.ioc.Container;
import com.szw.ioc.Inject;

public class RegexAdvisor implements Advisor {
	
	private Pointcut pointcut;
	private String pointcutName;
	private Advice advice;
	@Inject("container")
	private Container container;
	
	public RegexAdvisor(Pointcut pointcut,Advice advice){
		this.pointcut=pointcut;
		this.advice=advice;
	}
	
	public RegexAdvisor(String pointcut,Advice advice){
		this.pointcutName=pointcut;
		this.advice=advice;
	}
	
	@Override
	public Pointcut getPointcut() {
		return pointcut!=null?pointcut:container.getInstance(pointcutName, Pointcut.class);
	}

	@Override
	public Advice getAdvice() {
		return advice;
	}
	
	public int hashCode(){
		return advice.hashCode()*16+getPointcut().hashCode();
	}
	
	public boolean equals(Object o){
		if(!(o instanceof RegexAdvisor)) return false;
		
		if(o==this) return true;
		
		RegexAdvisor advisor=((RegexAdvisor)o);
		
		if(advisor.getPointcut()==pointcut&&advisor.getAdvice()==advice) return true;
		
		return false;
	}

	@Override
	public int getOrdered() {
		return ADVISOR_ORDER;
	}

}
