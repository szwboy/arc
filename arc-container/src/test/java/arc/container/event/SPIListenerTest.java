package arc.container.event;

import org.junit.Test;
import arc.container.Container;

public class SPIListenerTest {

	@Test
	public void test() {
		Container container= new Container("/annotation.xml", false);
		container.refresh();
	}

}
