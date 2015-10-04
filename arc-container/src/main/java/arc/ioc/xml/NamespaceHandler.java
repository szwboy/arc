package arc.ioc.xml;

import org.w3c.dom.Element;

public interface NamespaceHandler {

	void init();
	
	void paser(Element e,ParserContext parserContext);
}
