package arc.components.xml;

import org.w3c.dom.Element;

public interface NamespaceHandler {

	void init();
	
	void parse(Element e,ParserContext parserContext);
}
