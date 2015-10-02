package arc.ioc.container;

public class InternalContext {

	private Container container;
	private ExternalContext<?> externalContext;
	
	public InternalContext(Container container) {
		this.container = container;
	}

	InternalContext(){}
	
	public Container getContainer() {
		return container;
	}

	public ExternalContext<?> getExternalContext() {
		return externalContext;
	}

	public void setExternalContext(ExternalContext<?> externalContext) {
		this.externalContext = externalContext;
	}

}
