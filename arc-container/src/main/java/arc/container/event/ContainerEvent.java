package arc.container.event;

import java.util.EventObject;

import arc.container.Container;

public abstract class ContainerEvent extends EventObject {

	private static final long serialVersionUID = -5659675292619082075L;
	private long timestamp;

	public ContainerEvent(Container source) {
		super(source);
		this.timestamp= System.currentTimeMillis();
	}

	public long getTimestamp(){
		return this.timestamp;
	}
	
	
}
