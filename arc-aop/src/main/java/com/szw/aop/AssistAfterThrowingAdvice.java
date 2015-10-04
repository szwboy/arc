package com.szw.aop;

import com.szw.ClassUtils;
import com.szw.StringUtils;
import com.szw.ioc.Inject;

public class AssistAfterThrowingAdvice extends AbstractMethodAdvice implements AfterAdvice,MethodInterceptor{
	
	private String throwing;
	private Class<?> throwingClass;
	
	public AssistAfterThrowingAdvice(String method,String aspect,String throwing){
		super(method,aspect);
		this.throwing=throwing;
	}

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable{
		try{
			return invocation.proceed();
		}catch(Throwable t){
			if(throwingClass!=null){
				if(shouldBeThrowable(t.getClass()))
					refMethod.invoke(ref, t);
			}else refMethod.invoke(ref);	
			
			throw t;
		}
	}
	
	private boolean shouldBeThrowable(Class<?> t){
		return throwingClass.isAssignableFrom(t);
	}

	@Inject
	protected void init() {
		
		if(!StringUtils.isBalank(throwing)){
			if(throwing.equals("$")){
				Class<?>[] pts=refMethod.getParameterTypes();
				if(pts==null||pts.length==0) throw new RuntimeException(refMethod.getName()+"should have args");
				
				throwingClass=pts[0];
			}else
				throwingClass=ClassUtils.forName(throwing);
		}	
	}
	
	public boolean equals(Object o){
		if(!(o instanceof AssistAfterThrowingAdvice)){
			return false;
		}
		
		if(this==o) return true;
		
		AssistAfterThrowingAdvice advice=((AssistAfterThrowingAdvice)o);
		if(advice.getRefArgs()==this.getRefArgs()&&advice.getBindingArgs()==
				this.getBindingArgs()&&this.refMethod==advice.refMethod) return true;
		
		return false;
	}

}
