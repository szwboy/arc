package arc.core.xml;

import java.util.StringTokenizer;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 
 * @author sunzhongwei
 *
 */
public class ConfigParserDelegate{
	private final static String DEFAULT_NAMESPACE_URI="http://www.arc.com/schema/beans";
	
	private ReaderContext readerContext;
	
	private static final String IMPORT_ELEMENT="import";
	private static final String BEAN_ELEMENT="bean";
	private static final String CONST_ELEMENT="const";
	private static final String DEFAULT_SPLIT=",;";

	private void parse(Element e) throws Exception {
		Configuration config;
		if(isNodeEquals(IMPORT_ELEMENT,e)){
			String resourcePaths=e.getAttribute("resource");
			StringTokenizer stringTokenizer=new StringTokenizer(resourcePaths,DEFAULT_SPLIT);
			while(stringTokenizer.hasMoreTokens()){
				String resourcePath=stringTokenizer.nextToken();
				if(StringUtils.isBlank(resourcePath)) continue;
				
				readerContext.getReader().loadDefinition(resourcePath);
				
			}
			
		}else if(isNodeEquals(BEAN_ELEMENT,e)){
			config=parseBeanConfig(e);
			readerContext.getLoader().factory(config);
		}else if(isNodeEquals(CONST_ELEMENT,e)){
			config=parseConstConfig(e);
			readerContext.getLoader().constant(config);
		}
	}
	
	private Configuration parseConstConfig(Element e){
		
		String clazz=e.getAttribute("class");
		Configuration config=new Configuration(clazz);
		
		String name=e.getAttribute("name");
		if(!StringUtils.isBlank(name)){
			config.setName(name);
		}
		
		String value=e.getAttribute("value");
		if(!StringUtils.isBlank(value)){
			config.setValue(value);
		}
		
		return config;
	}
	
	private Configuration parseBeanConfig(Element e) throws ClassNotFoundException{
		String clazz=e.getAttribute("class");
		Configuration config=new Configuration(clazz);
		
		String name=e.getAttribute("name");
		if(!StringUtils.isBlank(name)){
			config.setName(name);
		}
		
		String type=e.getAttribute("type");
		if(!StringUtils.isBlank(type)){
			config.setType(type);
		}
		
		return config;
	}
	
	public boolean isNodeEquals(String name,Element e){
		return StringUtils.isNotBlank(e.getLocalName())&&name.equals(e.getLocalName());
	}

	public ConfigParserDelegate(ReaderContext readerContext){
		this.readerContext=readerContext;
	}
	
	public void parseConfig(Element root) throws Exception {
		NamespaceHandler handler=null;
		
		if(isDefaultNamespace(root.getNamespaceURI())){
			NodeList nodes=root.getChildNodes();
			for(int i=0;i<nodes.getLength();i++){
				Node node=nodes.item(i);
				if(node instanceof Element){
					Element e=(Element)node;
					if(isDefaultNamespace(e.getNamespaceURI())){
						parse(e);
					}else{
						handler=readerContext.getHandlerResolver().resolve(e.getNamespaceURI());
						if(handler!=null)	
							handler.paser(e, createPraserContext());
					}
				}
			}
		}else{
			handler=readerContext.getHandlerResolver().resolve(root.getNamespaceURI());
			if(handler!=null)
				handler.paser(root, createPraserContext());
		}
		
	}
	
	private ParserContext createPraserContext(){
		return new ParserContext(readerContext,this);
	}
	
	private boolean isDefaultNamespace(String namespaceUri){
		return StringUtils.isBlank(namespaceUri)||namespaceUri.equals(DEFAULT_NAMESPACE_URI);
	}

}
