package arc.aop.config;

import arc.components.xml.NamespaceHandlerSupport;

public class AopNamespaceHandler extends NamespaceHandlerSupport {

	
	@Override
	public void init() {
		super.registerNamespaceParser("aspect-autoproxy", new AopConfigParser());
	}

	
}
