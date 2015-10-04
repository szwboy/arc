package arc.core.xml;

import arc.ioc.container.ContainerLoader;



public class ReaderContext {

	private NamespaceHandlerResolver handlerResolver;
	private IConfigReader reader;
	private ContainerLoader loader;
	
	public ReaderContext(NamespaceHandlerResolver handlerResolver, IConfigReader reader,ContainerLoader loader) {
		this.handlerResolver = handlerResolver;
		this.reader = reader;
		this.loader=loader;
	}
	
	public NamespaceHandlerResolver getHandlerResolver() {
		return handlerResolver;
	}
	
	public IConfigReader getReader() {
		return reader;
	}
	
	public ContainerLoader getLoader(){
		return loader;
	}
	
}
