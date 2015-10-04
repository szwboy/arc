package arc.core.xml;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Element;

public abstract class NamespaceHandlerSupport implements NamespaceHandler {
	
	private final Map<String,ConfigParser> namespaceParsers=new HashMap<String,ConfigParser>();
	
	@Override
	public void paser(Element e,ParserContext parserContext) {
		getParserByElement(e).parser(e,parserContext);
	}
	
	protected void registerNamespaceParser(String elementName,ConfigParser parser){
		namespaceParsers.put(elementName, parser);
	}
	
	private ConfigParser getParserByElement(Element e){
		String elementName=e.getLocalName();
		ConfigParser parser=namespaceParsers.get(elementName);
		return parser;
	}
	
}
