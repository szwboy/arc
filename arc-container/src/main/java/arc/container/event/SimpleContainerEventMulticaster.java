package arc.container.event;

import arc.components.factory.ComponentFactory;

public class SimpleContainerEventMulticaster implements ContaienrEventMulticaster {
	private ComponentFactory componentFactory;
	
	public SimpleContainerEventMulticaster(ComponentFactory componentFactory){
		this.componentFactory= componentFactory;
	}

	@Override
	public void addListener(ContainerListener<? extends ContainerEvent> listener) {
	}

	@Override
	public void removeListener(
			ContainerListener<? extends ContainerEvent> listener) {
	}

	@Override
	public void addListenerComponentName(String name) {
	}

	@Override
	public void removeListenerComponentName(String name) {
	}

	@Override
	public void multicastEvent(ContainerEvent eve) {
	}

}
