package arc.components.factory;


public class InternalContext {

	private ComponentFactory componentFactory;
	private ExternalContext<?> externalContext;
	
	public InternalContext(ComponentFactory componentFactory) {
		this.componentFactory = componentFactory;
	}

	InternalContext(){}
	
	public ComponentFactory getComponentFactory() {
		return componentFactory;
	}

	public ExternalContext<?> getExternalContext() {
		return externalContext;
	}

	public void setExternalContext(ExternalContext<?> externalContext) {
		this.externalContext = externalContext;
	}

}
