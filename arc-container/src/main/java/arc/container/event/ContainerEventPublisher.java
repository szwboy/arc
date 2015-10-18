package arc.container.event;

public interface ContainerEventPublisher {

	String CONTAINER_EVENT_LOCATION="/container-events.xml";
	void publishEvent(ContainerEvent eve);
}
