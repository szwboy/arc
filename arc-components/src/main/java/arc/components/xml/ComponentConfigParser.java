package arc.components.xml;

import org.w3c.dom.Element;

public interface ComponentConfigParser {
	ComponentConfigParser parser(Element e,ParserContext parserContext);
}
