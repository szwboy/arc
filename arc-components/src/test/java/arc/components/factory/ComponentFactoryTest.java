package arc.components.factory;

import static org.junit.Assert.*;
import junit.framework.Assert;

import org.junit.Test;

import arc.components.support.ComponentRegistry;
import arc.components.xml.ComponentReader;
import arc.components.xml.XmlComponentReader;

public class ComponentFactoryTest {

	@Test
	public void test() {
		try {
			ComponentRegistry registry= new RegistrableComponentFactory();
			ComponentReader reader= new XmlComponentReader(registry);
			reader.loadDefinition("/arc/components/factory/ComponentFactoryReaderTest.xml");
			Assert.assertTrue(registry.containesFactory("a"));
			Assert.assertTrue(registry.containesFactory("b"));
			
			A a= ((ComponentFactory)registry).getComponent("a", A.class);
			B b= a.getB();
			b.getA().sayHello();
			a.getB().sayHello();
			Assert.assertTrue(a== b.getA());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
