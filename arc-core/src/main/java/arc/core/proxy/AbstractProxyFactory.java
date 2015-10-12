package arc.core.proxy;


public abstract class AbstractProxyFactory implements ProxyFactory {
	private InvocationHandler handler;
	
	@Override
	public <T> T getProxy(Class<T> ifc) {
		
		return doProxy(ifc);
	}
	
	protected abstract <T>T doProxy(Class<T> ifc);
	
	public void setHandler(InvocationHandler handler){
		this.handler= handler;
	}

	public InvocationHandler getHandler(){
		return handler;
	}
}
