package com.szw.aop;

import java.lang.reflect.Method;

public class AssistMethodBeforeAdvice extends AbstractMethodAdvice implements MethodBeforeAdvice {
	
	public AssistMethodBeforeAdvice(String method,String aspectName,String refArgs){
		super(method,aspectName,refArgs);
	}
	
	public AssistMethodBeforeAdvice(String method,String aspectName){
		super(method,aspectName,null);
	}

	@Override
	public void before(final Method method, Object[] args, Object target) throws Throwable{
		refMethod.invoke(ref, bindingArgs);
	}
	
	public boolean equals(Object o){
		if(!(o instanceof AssistMethodBeforeAdvice)){
			return false;
		}
		
		if(this==o) return true;
		
		AssistMethodBeforeAdvice advice=((AssistMethodBeforeAdvice)o);
		if(advice.getRefArgs()==this.getRefArgs()&&advice.getBindingArgs()==
				this.getBindingArgs()&&this.refMethod==advice.refMethod) return true;
		
		return false;
	}

}
