package arc.container.event;

import arc.container.Container;

public class ContainerStoppedEvent extends ContainerEvent{
	
	private static final long serialVersionUID = 6204770672849900683L;
	public ContainerStoppedEvent(Container source) {
		super(source);
	}

}
