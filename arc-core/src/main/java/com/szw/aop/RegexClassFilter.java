package com.szw.aop;



public class RegexClassFilter extends RegexHelper implements ClassFilter {
	

	public RegexClassFilter(String expression){
		super(expression);
	}
	
	@Override
	public boolean matches(Class<?> clazz) {
		String clzName=clazz.getName();
		if(!matches(clzName)){
			if(expression.indexOf("\\.")>-1)
				return find(clzName);
			else return false;
		}
		
		return true;
	}
	
}
