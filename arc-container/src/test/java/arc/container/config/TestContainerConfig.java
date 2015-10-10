package arc.container.config;

import static org.junit.Assert.*;

import org.junit.Test;

import arc.components.factory.RegistrableComponentFactory;
import arc.components.support.ComponentRegistry;
import arc.components.xml.ComponentReader;
import arc.components.xml.XmlComponentReader;
import arc.container.Container;

public class TestContainerConfig {

	@Test
	public void test() {
		ComponentRegistry registry= new RegistrableComponentFactory();
		ComponentReader reader= new XmlComponentReader(registry);
		reader.loadDefinition("/annotation.xml");
		registry.containesFactory("testBean", TestBean.class);
	}

}
