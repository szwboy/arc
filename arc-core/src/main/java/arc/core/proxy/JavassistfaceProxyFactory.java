package arc.core.proxy;

import arc.core.util.ReflectUtils;

public class JavassistfaceProxyFactory extends ProxyFactorySupport {
	
	@SuppressWarnings("unchecked")
	public <T> T getProxy(Class<T> ifc) {
		Class<? super T>[] ics= ReflectUtils.getAllInterfaces(ifc);
		return (T)Proxy.getProxy(ics).newInstance(getHandler());
	}

}
