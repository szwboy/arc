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
	public static final String CONST_ELEMENT="constant";
	public static final String DEFAULT_SPLIT=",;";
	
	public static final String CLASS_ATTRIBUTE="class";
	public static final String IMPL_ATTRIBUTE="impl";
	public static final String SCOPE_ATTRIBUTE="scope";
	public static final String ID_ATTRIBUTE="id";
	public static final String VALUE_ATTRIBUTE="value";
	public static final String RESOURCE_ATTRIBUTE="resource";
	
	@SuppressWarnings("unchecked")
	private void parse(Element e){
		if(isNodeEquals(IMPORT_ELEMENT,e)){
			String resourcePaths=e.getAttribute(RESOURCE_ATTRIBUTE);
			StringTokenizer stringTokenizer=new StringTokenizer(resourcePaths,DEFAULT_SPLIT);
			while(stringTokenizer.hasMoreTokens()){
				String resourcePath=stringTokenizer.nextToken();
				if(StringUtils.isNotBlank(resourcePath)) readerContext.getReader().loadDefinition(resourcePath);
			}
			
		}else if(isNodeEquals(COMPONENT_ELEMENT,e)){
			String impl=e.getAttribute(IMPL_ATTRIBUTE);
			Component<?> component = createComponent(impl);
			component.accept(new ComponentAttributeVisitor(e));
			readerContext.getRegistry().factory(component.getId(), component.getType(), component.getScope());
		}else if(isNodeEquals(CONST_ELEMENT,e)){
			String type=e.getAttribute(CLASS_ATTRIBUTE);
			
			if(StringUtils.isBlank(type)){
				type= e.getAttribute(IMPL_ATTRIBUTE);
			}
			Component constant=createComponent(type);
			constant.accept(new ConstantVisitor(e));
			readerContext.getRegistry().constant(constant.getId(), constant.getValue(), constant.getType(), constant.getImpl());
		}
	}
	
	@SuppressWarnings("unchecked")
	private <T>Component<T> createComponent(String type){
		try {
			return new Component<T>(ClassUtils.getClass(type));
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException("cannot get the class of "+type, e);
		}
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
							handler.parse(e, createPraserContext());
					}
				}
			}
		}else{
			handler=readerContext.getHandlerResolver().resolve(root.getNamespaceURI());
			if(handler!=null)
				handler.parse(root, createPraserContext());
		}
		
	}
	
	private ParserContext createPraserContext(){
		return new ParserContext(readerContext,this);
	}
	
	private boolean isDefaultNamespace(String namespaceUri){
		return StringUtils.isBlank(namespaceUri)||namespaceUri.equals(DEFAULT_NAMESPACE_URI);
	}

}
