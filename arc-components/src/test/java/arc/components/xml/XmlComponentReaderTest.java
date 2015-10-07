package arc.components.xml;

import junit.framework.Assert;
import junit.framework.TestCase;
import arc.components.factory.RegistrableComponentFactory;
import arc.components.support.ComponentRegistry;
import arc.components.xml.ComponentReader;
import arc.components.xml.XmlComponentReader;

public class XmlComponentReaderTest extends TestCase {

	public void testLoadDefinitions(){
		try {
			ComponentRegistry registry= new RegistrableComponentFactory();
			ComponentReader reader= new XmlComponentReader(registry);
			reader.loadDefinition("/arc/components/xml/XmlConfigReaderTest.xml");
			Assert.assertTrue(registry.containesFactory("a", A.class));
			Assert.assertTrue(registry.containesFactory("school", String.class));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
