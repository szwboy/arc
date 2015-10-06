package arc.components.factory;



public class ExternalContext<T> {

	private Container container;
	private Key<T> key;
	
	public ExternalContext(Container container, Key<T> key) {
		this.container = container;
		this.key = key;
	}

	public Container getContainer() {
		return container;
	}

	public Key<T> getKey() {
		return key;
	}
	
	
}
