package arc.aop;

import org.aopalliance.intercept.MethodInvocation;

public interface ProxyMethodInvocation extends MethodInvocation {

	Object getProxy();
	
	Object getUserAttribute(String expression);
	
	void setUserAttribute(String expression, Object value);
}
