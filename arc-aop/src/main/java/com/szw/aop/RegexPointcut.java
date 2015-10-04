package com.szw.aop;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

public class RegexPointcut implements Pointcut,MethodMatcher {

	private Pattern pattern;
	private ClassFilter classFilter;
	
	public RegexPointcut(String expression){
		this.pattern=Pattern.compile(expression);
		this.classFilter=new RegexClassFilter(expression);
	}
	@Override
	public ClassFilter getClassFilter() {
		return classFilter;
	}

	@Override
	public MethodMatcher getMethodMatcher() {
		return this;
	}

	@Override
	public boolean matches(Method method,Class<?> targetClass) {
		return pattern.matcher(targetClass!=null?targetClass.getName()+"."+method.getName():
			method.getDeclaringClass().getName()+"."+method.getName()).matches();
	}
	
	@Override
	public boolean matches(Method method,Class<?> targetClass,Class<?>[] paramTypes) {
		StringBuilder methodName=new StringBuilder(method.getName()).append("(");
		if(paramTypes!=null&&paramTypes.length>0){
			for(int i=0;i<paramTypes.length;i++){
				methodName.append(paramTypes[i].getName());
				
				if(i<paramTypes.length-1) methodName.append(",");
			}
		}
		methodName.append(")");
	
		return pattern.matcher((targetClass!=null?targetClass.getName():method.getDeclaringClass().getName())
								+"."+methodName).matches();
	}
	
	public int getOrdered(){
		return POINTCUT_ORDER;
	}

}
