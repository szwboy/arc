package arc.ioc.xml;

import junit.framework.TestCase;

public class XmlConfigReaderTest extends TestCase {

	public void testLoadDefinitions(){
		try {
			ConfigReader reader= new XmlConfigReader("/arc/ioc/xml/XmlConfigReaderTest.xml");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
