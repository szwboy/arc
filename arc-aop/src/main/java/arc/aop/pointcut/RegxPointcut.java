package com.arc.aop.pointcut;

import java.util.regex.Pattern;

public class RegxPointcut implements Pointcut {
	
	private String expression;

	private Pattern[] pattern= new Pattern[0];
	
	@Override
	public MethodMatcher getMethodMatcher() {
		// TODO Auto-generated method stub
		return null;
	}

}
