package arc.core.proxy;

public abstract class ProxyFactorySupport implements ProxyFactory {
	private InvocationHandler<?> handler;
	
	public void setHandler(InvocationHandler<?> handler){
		this.handler= handler;
	}

	public InvocationHandler<?> getHandler(){
		return handler;
	}
}
