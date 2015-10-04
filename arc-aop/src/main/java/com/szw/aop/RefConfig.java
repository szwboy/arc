package com.szw.aop;

import java.util.StringTokenizer;

import com.szw.StringUtils;

class RefConfig {

	String[] refArgs;
	String	method;
	String aspect;
	String[] refArgTypes;
	
	protected RefConfig(String method,String aspect,String refArgs){
		int firstIndex=method.indexOf("(");
		int lastIndex=method.lastIndexOf(")");
		
		if((firstIndex==-1&&lastIndex!=-1)||(firstIndex!=-1&&lastIndex==-1))
			throw new RuntimeException("'(' and ')' can't be singlely existed");
		
		this.aspect=aspect;
		
		StringTokenizer token=new StringTokenizer(method,"()");
		int count=token.countTokens();
		for(int i=0;i<count;i++){
			if(i==0){
				this.method=token.nextToken();
				continue;
			}	
			
			if(i==1){
				String arg=token.nextToken();
				this.refArgTypes=StringUtils.isBalank(arg)?null:arg.split(",");
				break;
			}	
		}
		
		if(refArgs!=null)
			this.refArgs=refArgs.trim().split(",");
	}

	public String[] getRefArgs() {
		return refArgs;
	}
	
	
}
