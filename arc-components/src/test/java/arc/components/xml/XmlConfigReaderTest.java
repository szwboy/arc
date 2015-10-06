package arc.components.xml;

import junit.framework.TestCase;
import arc.components.factory.RegistrableContainer;
import arc.components.support.ComponentRegistry;
import arc.components.xml.ComponentReader;
import arc.components.xml.XmlComponentReader;

public class XmlConfigReaderTest extends TestCase {

	public void testLoadDefinitions(){
		try {
			ComponentRegistry registry= new RegistrableContainer();
			ComponentReader reader= new XmlComponentReader(registry);
			reader.loadDefinition("/arc/components/xml/XmlConfigReaderTest.xml");
			System.out.println(registry.containesComponent("a", A.class));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
