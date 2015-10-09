package arc.container.event;

import arc.container.Container;

public class ContainerRefreshedEvent extends ContainerEvent {

	private static final long serialVersionUID = 6881268828474496342L;
	
	private Container source;
	public ContainerRefreshedEvent(Container source) {
		super(source);
		this.source= source;
	}

}
