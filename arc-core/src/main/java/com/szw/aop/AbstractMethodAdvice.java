package com.szw.aop;

import java.lang.reflect.Method;
import java.util.StringTokenizer;

import com.szw.ClassUtils;
import com.szw.StringUtils;
import com.szw.ioc.BeanPostProcessor;
import com.szw.ioc.Container;
import com.szw.ioc.Inject;

public abstract class AbstractMethodAdvice extends RefConfig implements MethodAdvice{
	protected Method refMethod;
	protected Object ref;
	protected Object[] bindingArgs=new Object[0];

	@Inject("container")
	protected Container container;
	
	public AbstractMethodAdvice(String method,String aspect){
		this(method,aspect,null);
	}
	
	public AbstractMethodAdvice(String method,String aspect,String refArgs){
		super(method,aspect,refArgs);
	}

	public void bindingArgs(Object[] args){
		this.bindingArgs=args;
	}
		
	public Object[] getBindingArgs(){
		return bindingArgs;
	}
	
	@Inject
	public void inject() throws SecurityException, NoSuchMethodException {
		ref=container.getInstance(aspect, null);
		refMethod=getMethod(ref.getClass(),method);
	}
	
	private Method getMethod(Class<?> c,String method){
		if(refArgTypes!=null){
			Class<?>[] paramTypes=new Class<?>[refArgTypes.length];
			for(int i=0;i<paramTypes.length;i++){
				paramTypes[i]=ClassUtils.forName(refArgTypes[i]);
			}
		}
		
		if(refArgTypes==null||refArgTypes.length==0){
			try {
				if(c.getMethod(method)!=null) return c.getMethod(method);
			} catch (SecurityException e) {
				throw new RuntimeException(e);
			} catch (NoSuchMethodException e) {
				return getMinimalParameterMethod(c.getMethods(),method);
			}
			
		}	
		
		Class<?>[] parameterTypes=new Class<?>[refArgTypes.length];
		int i=0;
		for(String arg:refArgTypes){
			parameterTypes[i++]=ClassUtils.forName(arg);
		}
		
		try {
			return c.getMethod(method, parameterTypes);
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}
	
	private Method getMinimalParameterMethod(Method[] methods,String methodName){
		
		Method m=null;
		for(Method method:methods){
			if(method.getName().equals(methodName)){
				
				int lastParamCount=m==null?Integer.MAX_VALUE:m.getParameterTypes().length;
				if(lastParamCount>method.getParameterTypes().length){
					m=method;
				}
			}
		}
		
		return m;
	}
	
	public int hashCode(){
		return ref.hashCode()*16+refMethod.hashCode()*8+bindingArgs.hashCode();
	}
	
}
