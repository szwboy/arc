package arc.container.event;

import arc.components.factory.Key;


public interface ContaienrEventMulticaster {

	void addListener(ContainerListener<ContainerEvent> listener);
	
	void removeListener(ContainerListener<ContainerEvent> listener);
	
	void addListenerComponentName(String name);
	
	void removeListenerComponentName(String name);
	
	<T extends ContainerEvent>void multicastEvent(T eve);
}
