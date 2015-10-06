package arc.components.xml;

import java.util.StringTokenizer;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 
 * @author sunzhongwei
 *
 */
public class ComponentConfigParserDelegate{
	private final static String DEFAULT_NAMESPACE_URI="http://www.arc.com/schema/components";
	
	private ReaderContext readerContext;
	
	public static final String IMPORT_ELEMENT="import";
	public static final String COMPONENT_ELEMENT="component";
	public static final String CONST_ELEMENT="const";
	public static final String DEFAULT_SPLIT=",;";
	
	public static final String CLASS_ATTRIBUTE="class";
	public static final String IMPL_ATTRIBUTE="impl";
	public static final String SCOPE_ATTRIBUTE="scope";
	public static final String ID_ATTRIBUTE="id";
	public static final String VALUE_ATTRIBUTE="value";
	
	private static final Logger log= Logger.getLogger(ComponentConfigParserDelegate.class);

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void parse(Element e){
		if(isNodeEquals(IMPORT_ELEMENT,e)){
			String resourcePaths=e.getAttribute("resource");
			StringTokenizer stringTokenizer=new StringTokenizer(resourcePaths,DEFAULT_SPLIT);
			while(stringTokenizer.hasMoreTokens()){
				String resourcePath=stringTokenizer.nextToken();
				if(StringUtils.isBlank(resourcePath)) continue;
				
				readerContext.getReader().loadDefinition(resourcePath);
				
			}
			
		}else if(isNodeEquals(COMPONENT_ELEMENT,e)){
			String impl=e.getAttribute("impl");
			try {
				Component component = parseComponent(e, ClassUtils.getClass(impl));
				readerContext.getRegistry().factory(component.getId(), component.getImpl(), component.getScope());
			} catch (ClassNotFoundException e1) {
				log.error(e);
			}
		}else if(isNodeEquals(CONST_ELEMENT,e)){
			Component<?> component=parseConst(e);
		}
	}
	
	private <T>Component<T> parseConst(Element e){
		
//		String clazz=e.getAttribute("class");
//		Component<T> config= new Component<T>(clazz);
//		
//		String id= e.getAttribute("id");
//		if(StringUtils.isNotBlank(id)){
//			config.setId(id);
//		}
//		
//		String impl= e.getAttribute("impl");
//		if(StringUtils.isNotBlank(impl)){
//			config.setImpl(impl);
//		}
//		
//		String scope= e.getAttribute("scope");
//		if(StringUtils.isNotBlank(scope)){
//			config.setImpl(scope);
//		}
		
		return null;
	}
	
	private <T>Component<T> parseComponent(Element e, Class<T> impl){
		Component<T> component= new Component<T>(impl);
		
		String id= e.getAttribute(ID_ATTRIBUTE);
		if(StringUtils.isNotBlank(id)){
			component.setId(id);
		}
		
		String scope= e.getAttribute(SCOPE_ATTRIBUTE);
		if(StringUtils.isNotBlank(scope)){
			component.setScope(scope);
		}
		
		return component;
	}
	
	public boolean isNodeEquals(String name,Element e){
		return StringUtils.isNotBlank(e.getLocalName())&&name.equals(e.getLocalName());
	}

	public ComponentConfigParserDelegate(ReaderContext readerContext){
		this.readerContext=readerContext;
	}
	
	public void parseConfig(Element root){
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
