package arc.ioc.xml;

import org.w3c.dom.Element;

public interface ConfigParser {
	Configuration parser(Element e,ParserContext parserContext);
}
