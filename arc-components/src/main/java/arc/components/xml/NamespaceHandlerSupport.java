package arc.components.xml;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Element;


public abstract class NamespaceHandlerSupport implements NamespaceHandler {
	
	private final Map<String, ComponentConfigParser> namespaceParsers=new HashMap<String, ComponentConfigParser>();
	
	@Override
	public void paser(Element e,ParserContext parserContext) {
		getParserByElement(e).parser(e,parserContext);
	}
	
	protected void registerNamespaceParser(String elementName, ComponentConfigParser parser){
		namespaceParsers.put(elementName, parser);
	}
	
	private ComponentConfigParser getParserByElement(Element e){
		String elementName=e.getLocalName();
		ComponentConfigParser parser=namespaceParsers.get(elementName);
		return parser;
	}
	
}
