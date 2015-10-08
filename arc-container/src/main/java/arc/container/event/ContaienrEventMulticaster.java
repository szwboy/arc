package arc.container.event;


public interface ContaienrEventMulticaster {

	void addListener(ContainerListener<? extends ContainerEvent> listener);
	
	void removeListener(ContainerListener<? extends ContainerEvent> listener);
	
	void addListenerComponentName(String name);
	
	void removeListenerComponentName(String name);
	
	void multicastEvent(ContainerEvent eve);
}
