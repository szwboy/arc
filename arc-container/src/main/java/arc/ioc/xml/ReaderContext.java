package arc.ioc.xml;

import arc.ioc.container.ContainerLoader;


public class ReaderContext {

	private NamespaceHandlerResolver handlerResolver;
	private ConfigReader reader;
	private ContainerLoader loader;
	
	public ReaderContext(NamespaceHandlerResolver handlerResolver, ConfigReader reader,ContainerLoader loader) {
		this.handlerResolver = handlerResolver;
		this.reader = reader;
		this.loader=loader;
	}
	
	public NamespaceHandlerResolver getHandlerResolver() {
		return handlerResolver;
	}
	
	public ConfigReader getReader() {
		return reader;
	}
	
	public ContainerLoader getLoader(){
		return loader;
	}
	
}
