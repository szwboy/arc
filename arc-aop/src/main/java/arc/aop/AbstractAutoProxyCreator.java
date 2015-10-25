package arc.aop;

import arc.aop.autoproxy.ProxyConfig;
import arc.components.factory.ComponentPostProcessor;

@SuppressWarnings("rawtypes")
public abstract class AbstractAutoProxyCreator extends ProxyConfig implements ComponentPostProcessor {

	/*-------------------------------------------------------------------------
	 *implementation of component post processor
	 *------------------------------------------------------------------------*/
	public Object posProcessorBeforeInitialization(Object t, String beanName) {
		return null;
	}

	@Override
	public Object postProcessorAfterInitialization(Object t, String beanName) {
		
		return null;
	}
	
	private 

}
