package arc.core.proxy;

import java.lang.reflect.Method;

public class AopInvocationHandler implements InvocationHandler {

	private Object target;
	@Override
	public Object invoke(Method method, Object[] args, Object proxy) {
		String mn= method.getName();
		
		if(mn.equals("toString")) return target.toString();
		if(mn.equals("equals")) return target.equals(args[0]);
		if(mn.equals("hashCode")) return target.hashCode();
		
		return null;
	}
	
	public AopInvocationHandler(Object target){
		this.target= target;
	}
	

}
