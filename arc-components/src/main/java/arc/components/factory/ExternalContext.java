package arc.components.factory;



public class ExternalContext<T> {

	private ComponentFactory componentFactory;
	private Key<T> key;
	
	public ExternalContext(ComponentFactory componentFactory, Key<T> key) {
		this.componentFactory = componentFactory;
		this.key = key;
	}

	public ComponentFactory getContainer() {
		return componentFactory;
	}

	public Key<T> getKey() {
		return key;
	}
	
	
}
