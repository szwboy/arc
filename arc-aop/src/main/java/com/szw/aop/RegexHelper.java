package com.szw.aop;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexHelper {
	private Pattern pattern;
	protected String expression;

	public RegexHelper(String expression){
		setPattern(expression);
		this.expression=expression;
	}
	
	protected void setPattern(String expression){
		pattern=Pattern.compile(expression);
	}
	
	protected boolean matches(String s) {
		Matcher matcher=pattern.matcher(s);
		return matcher.matches();
	}
	
	protected boolean find(String s) {
		try{
			String ss=expression;
			if(expression.indexOf("\\(")>-1){
				ss=ss.substring(0,ss.lastIndexOf("\\("));
			}
			setPattern(expression.substring(0, ss.lastIndexOf("\\.")));
			Matcher matcher=pattern.matcher(s);
			return matcher.matches();
		}finally{
			setPattern(expression);
		}
	}
}
