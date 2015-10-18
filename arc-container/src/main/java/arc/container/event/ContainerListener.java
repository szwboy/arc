package arc.container.event;

import java.util.EventListener;

public interface ContainerListener<T extends ContainerEvent> extends EventListener {

	void onContainerEvent(T event);
	
	boolean support(T event);
}
