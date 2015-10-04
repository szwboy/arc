package com.szw.aop;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.szw.ClassUtils;
import com.szw.assist.AssistUtils;

public class ReflectiveMethodInvocation implements MethodInvocation{

	private Object[] args;
	private Object proxy;
	private Object target;
	private Method method;
	private MethodInterceptor[] interceptors;
	private int interceptorCount;
	
	public ReflectiveMethodInvocation(Object[] args, Object proxy, Object target,
			Method method,Advisor[] advisors) {
		this.args = args;
		this.proxy = proxy;
		this.target = target;
		this.method=method;
		this.interceptors=getInterceptor(advisors);
	}
	
	private MethodInterceptor[] getInterceptor(Advisor[] advisors){
		if(advisors==null||advisors.length==0) return null;
		
		List<MethodInterceptor> interceptors=new ArrayList<MethodInterceptor>();
		for(Advisor advisor:advisors){
			Pointcut pointcut=advisor.getPointcut();
			Advice advice=advisor.getAdvice();
			
			if(pointcut.getMethodMatcher().matches(method, target.getClass(), method.getParameterTypes())){
				
				if(getInterceptor(advice)!=null)
					interceptors.add(getInterceptor(advice));
			}else if(advice instanceof IntroductionAdvice){
				interceptors.add(getInterceptor(advice));
			}
		}
		
		return interceptors.toArray(new MethodInterceptor[0]);
	}
	
	private MethodInterceptor getInterceptor(Advice advice){
		if(advice instanceof MethodInterceptor) return (MethodInterceptor) advice;
		
		if(advice instanceof MethodBeforeAdvice)
			return new MethodBeforeInterceptor((MethodBeforeAdvice)advice);
		//q其他种类的advice待定
		else return null;
	}
	
	@Override
	public Object proceed() throws Throwable {
		if (interceptors==null||this.interceptorCount == this.interceptors.length) 
			return ClassUtils.invokeTargetMethod(method, target, args);
		
		MethodInterceptor interceptor=interceptors[interceptorCount++];
		if(interceptor instanceof AutoProxyMethodInterceptor){
			MethodAdvice advice=((AutoProxyMethodInterceptor)interceptor).getMethodAdvice();
			AssistUtils.bindingParams(target, (MethodAdvice)advice,args);
		}
		
		return interceptor.invoke(this);
	}
	
	
	
	@Override
	public Object getThis() {
		return this.target;
	}
	@Override
	public Method getStaticPart() {
		return this.method;
	}
	@Override
	public Object[] getArguments() {
		return this.args;
	}

	@Override
	public Method getMethod() {
		return this.method;
	}
	
	
}
