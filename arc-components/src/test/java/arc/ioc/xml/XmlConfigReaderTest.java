package arc.ioc.xml;

import junit.framework.TestCase;
import arc.components.xml.ComponentReader;
import arc.components.xml.XmlComponentReader;

public class XmlConfigReaderTest extends TestCase {

	public void testLoadDefinitions(){
		try {
			ComponentReader reader= new XmlComponentReader("/arc/components/xml/XmlConfigReaderTest.xml");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
