package arc.container.config;

import arc.container.event.ContainerListener;
import arc.container.event.ContainerRefreshedEvent;
import arc.core.stereotype.Service;

@Service("testListener")
public class TestContainerListener implements ContainerListener<ContainerRefreshedEvent> {

	@Override
	public void onContainerEvent(ContainerRefreshedEvent event) {
		System.out.println("container has refreshed:"+event.getClass().getSimpleName());
	}

	@Override
	public boolean support(ContainerRefreshedEvent event) {
		// TODO Auto-generated method stub
		return true;
	}

}
