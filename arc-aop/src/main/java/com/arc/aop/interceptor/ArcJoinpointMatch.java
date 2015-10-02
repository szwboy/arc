package com.arc.aop.interceptor;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;

public class ArcJoinpointMatch implements JoinpointMatch{
	
	private Method m;
	private PointcutParameter[] pointcutParams;
	
	public ArcJoinpointMatch(Method m){
		this.m= m;
	}
	
	public void argsBinding(){
		
		ClassPool pool= ClassPool.getDefault();
		try {
			CtClass cc= pool.get(m.getDeclaringClass().getName());
			CtMethod cm= cc.getDeclaredMethod(m.getName());
			
			MethodInfo methodInfo= cm.getMethodInfo();
			CodeAttribute codeAttribute= methodInfo.getCodeAttribute();
			LocalVariableAttribute attribute =(LocalVariableAttribute)codeAttribute.getAttribute(LocalVariableAttribute.tag);
			
			//非static的在codeattribute里传入的第一个参数是this
			int offset= Modifier.isStatic(cm.getModifiers())? 0:1;
			
			int len= cm.getParameterTypes().length;
			pointcutParams= new PointcutParameter[len];
			for(int i=0;i<len;i++){
				
				String name= attribute.variableName(i+offset);
				pointcutParams[i]= new DefaultPointcutParameter(name, i);
			}
		} catch (NotFoundException e) {
		}
	}

	@Override
	public PointcutParameter[] getParameterBindings() {
		return pointcutParams;
	}
	
	public interface PointcutParameter{
		
		String getName();
		
		int getParameterIndex();
	}
	
	class DefaultPointcutParameter implements PointcutParameter{

		private String name;
		private int index;
		
		public DefaultPointcutParameter(String name, int index){
			this.name= name;
			this.index= index;
		}
		
		@Override
		public String getName() {
			return this.name;
		}

		@Override
		public int getParameterIndex() {
			return this.index;
		}
		
	}
	
}
