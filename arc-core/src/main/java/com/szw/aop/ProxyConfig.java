package com.szw.aop;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class ProxyConfig {

	private Object target;
	private Class<?> targetClass;
	private Set<Advisor> advisors;
	private Set<Advice> introductionAdvices;
	
	public ProxyConfig(){
		advisors=new HashSet<Advisor>();
	}
	
	public void addAdvisor(Advisor advisor){
		if(advisors==null) advisors=new HashSet<Advisor>();
		advisors.add(advisor);
	}
	
	public void addAdvisor(List<Advisor> advisors){
		if(this.advisors==null) this.advisors=new HashSet<Advisor>();
		this.advisors.addAll(advisors);
	}
	
	public Advisor[] getAdvisors() {
		return advisors.toArray(new Advisor[0]);
	}
	
	public Object getTarget() {
		return target;
	}
	public void setTarget(Object target) {
		this.target = target;
	}
	public Class<?> getTargetClass() {
		return targetClass;
	}
	public void setTargetClass(Class<?> targetClass) {
		this.targetClass = targetClass;
	}

	public void addIntroductionAdvices(Advice introductionAdvice) {
		if(introductionAdvices==null) introductionAdvices=new HashSet<Advice>();
		this.introductionAdvices.add(introductionAdvice);
	}

	public Set<Advice> getIntroductionAdvices() {
		return introductionAdvices;
	}
	
	
}
