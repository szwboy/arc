package arc.container.config;

import arc.components.xml.NamespaceHandlerSupport;

public class ContainerNamespaceHandler extends NamespaceHandlerSupport {

	@Override
	public void init() {
		super.registerNamespaceParser("component-scan", new ScanComponentConfigParser());
	}

}
