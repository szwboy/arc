package arc.aop.interceptor;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class ExposedInvocationInterceptor implements MethodInterceptor {
	private static final ThreadLocal<MethodInvocation> invocation= new ThreadLocal<MethodInvocation>();

	@Override
	public Object invoke(MethodInvocation arg0) throws Throwable {
		return null;
	}
	
	public static MethodInvocation currentInvocation(){
		return invocation.get();
	}

}
