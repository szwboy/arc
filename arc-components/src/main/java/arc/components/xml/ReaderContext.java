package arc.components.xml;

import arc.components.support.ComponentRegistry;


public class ReaderContext {

	private NamespaceHandlerResolver handlerResolver;
	private ComponentReader reader;
	private ComponentRegistry registry;
	
	public ReaderContext(NamespaceHandlerResolver handlerResolver, ComponentReader reader, ComponentRegistry registry) {
		this.handlerResolver = handlerResolver;
		this.reader = reader;
		this.registry= registry;
	}
	
	public NamespaceHandlerResolver getHandlerResolver() {
		return handlerResolver;
	}
	
	public ComponentReader getReader() {
		return reader;
	}
	
	public ComponentRegistry getRegistry(){
		return registry;
	}
	
}
