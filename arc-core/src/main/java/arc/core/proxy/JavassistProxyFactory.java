package arc.core.proxy;

import arc.core.util.ClassUtils;

public class JavassistProxyFactory extends ProxyFactorySupport {
	
	@SuppressWarnings("unchecked")
	public <T> T getProxy(Class<T> ifc) {
		
		if(ifc.isInterface()){
			Class<? super T>[] ics= ClassUtils.getAllInterfaces(ifc);
			return (T)Proxy.getProxy(ics).newInstance(getHandler());
		}else{
			return (T)Proxy.getProxy(ifc).newInstance(getHandler());
		}
	}

}
