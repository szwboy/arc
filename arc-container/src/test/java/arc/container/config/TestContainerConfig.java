package arc.container.config;

import static org.junit.Assert.*;
import junit.framework.Assert;

import org.junit.Test;

import arc.components.factory.ComponentFactory;
import arc.components.factory.RegistrableComponentFactory;
import arc.components.support.ComponentRegistry;
import arc.components.xml.ComponentReader;
import arc.components.xml.XmlComponentReader;

public class TestContainerConfig {

	@Test
	public void test() {
		ComponentRegistry registry= new RegistrableComponentFactory();
		ComponentReader reader= new XmlComponentReader(registry);
		reader.loadDefinition("/annotation.xml");
		Assert.assertTrue(registry.containesFactory("testBean"));
		((ComponentFactory)registry).getComponent("testBean", TestBean.class).sayHello();
	}

}
