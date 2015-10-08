package arc.container.event;


public interface ContaienrEventMulticaster {

	void addListener(ContainerListener<ContainerEvent> listener);
	
	void removeListener(ContainerListener<ContainerEvent> listener);
	
	void addListenerComponentName(String name);
	
	void removeListenerComponentName(String name);
	
	void multicastEvent(ContainerEvent eve);
}
