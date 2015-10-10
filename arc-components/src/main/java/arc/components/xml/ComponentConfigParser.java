package arc.components.xml;

import org.w3c.dom.Element;

public interface ComponentConfigParser {
	void parse(Element e,ParserContext parserContext);
}
