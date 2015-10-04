package arc.core.proxy;


public abstract class AbstractProxyFactory implements ProxyFactory {
	
	@Override
	public <T> T getProxy(Class<T> ifc) {
		
		return doProxy(ifc);
	}
	
	protected abstract <T>T doProxy(Class<T> ifc);

}
