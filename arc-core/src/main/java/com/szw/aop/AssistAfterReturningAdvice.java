package com.szw.aop;

import java.util.Arrays;

public class AssistAfterReturningAdvice extends AbstractMethodAdvice implements
		AutoProxyMethodInterceptor, AfterAdvice {

	public AssistAfterReturningAdvice(String method, String aspect,String refArgs) {
		super(method, aspect,refArgs);
	}

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
			Object o = invocation.proceed();
			bindingArgs=Arrays.copyOf(bindingArgs, bindingArgs.length+1);
			bindingArgs[bindingArgs.length-1]=o;
			return refMethod.invoke(ref, bindingArgs);
	}

	@Override
	public MethodAdvice getMethodAdvice() {
		return this;
	}
	
	public boolean equals(Object o){
		if(!(o instanceof AssistAfterReturningAdvice)){
			return false;
		}
		
		if(this==o) return true;
		
		AssistAfterReturningAdvice advice=((AssistAfterReturningAdvice)o);
		if(advice.getRefArgs()==this.getRefArgs()&&advice.getBindingArgs()==
				this.getBindingArgs()&&this.refMethod==advice.refMethod) return true;
		
		return false;
	}
	
}
