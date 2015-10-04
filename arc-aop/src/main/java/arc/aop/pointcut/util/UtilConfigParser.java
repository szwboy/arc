package arc.context.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import arc.context.util.PropertyValue.ArrayPropertyValue;


/**
 * ��util��ǩ����ҽ���
 * @author sunzhongwei
 *
 */
public class UtilConfigParser implements ConfigParser {

	@Override
	public Configuration parser(Element e,ParserContext parserContext) {
		Configuration config=null;
		 if(parserContext.getParser().isNodeEquals("collection",e)){
			config=parseCollectionConfig(e,parserContext);
		}else if(parserContext.getParser().isNodeEquals("map",e)){
			config=parseMapConfig(e,parserContext);
		}else if(parserContext.getParser().isNodeEquals("array",e)){
			config=parseArrayConfig(e,parserContext);
		}

		 constant(parserContext,config);
		 
		 return config;
	}
	
	private Configuration parseArrayConfig(Element e,ParserContext parserContext){
		Configuration config=new Configuration(e.getAttribute("class")+"[]");
		String name=e.getAttribute("name");
		if(!StringUtils.isBalank(name)){
			config.setName(name);
		}
		
		NodeList nodes=e.getChildNodes();
		List<PropertyValue.Element> values=new ArrayList<PropertyValue.Element>();
		for(int i=0;nodes!=null&&i<nodes.getLength();i++){
			Node node=nodes.item(i);
			if(node instanceof Element&&parserContext.getParser().isNodeEquals("value", (Element)node)){
			
				String value=((Element)nodes.item(i)).getAttribute("ref");
				boolean isRef=true;
				if(StringUtils.isBalank(value)){
					NodeList nodeList=node.getChildNodes();
					for(int j=0;nodeList!=null&&j<nodeList.getLength();j++){
						if(nodeList.item(j) instanceof CharacterData)
							value=nodeList.item(j).getNodeValue();
							isRef=false;
					}
				}
				
				String type=StringUtils.isBalank(((Element)nodes.item(i)).getAttribute("type"))?e.getAttribute("class"):((Element)nodes.item(i)).getAttribute("type");
				values.add(new PropertyValue.Element(value,isRef,ClassUtils.forName(type)));
			}
		}
		
		PropertyValue propertyValue=new PropertyValue.ArrayPropertyValue(ClassUtils.forName(e.getAttribute("class")),values.toArray(new ArrayPropertyValue.Element[0]));
		config.setValue(propertyValue);
		
		return config;
	}
	
	private Configuration parseMapConfig(Element e,ParserContext parserContext){
		Configuration config=new Configuration("java.util.Map");
		
		if(e.hasAttribute("name")){
			config.setName(e.getAttribute("name"));
		}
		
		Map<PropertyValue.Element,PropertyValue.Element> values=new HashMap<PropertyValue.Element,PropertyValue.Element>();
		NodeList nodes=e.getChildNodes();
		for(int i=0;nodes!=null&&i<nodes.getLength();i++){
			Node node=nodes.item(i);
			if(node instanceof Element&&parserContext.getParser().isNodeEquals("entry",(Element)node)){
			
				PropertyValue.Element k=null;
				PropertyValue.Element v=null;
				NodeList ns=node.getChildNodes();
				for(int j=0;ns!=null&&j<ns.getLength();j++){
					if(!(ns.item(j) instanceof Element)||(!parserContext.getParser().isNodeEquals("key",(Element)ns.item(j))&&!parserContext.getParser().isNodeEquals("value",(Element)ns.item(j)))) continue;
					
					String value=((Element)ns.item(j)).getAttribute("ref");
					boolean isRef=true;
					if(StringUtils.isBalank(value)){
						NodeList nodeList=ns.item(j).getChildNodes();
						for(int n=0;nodeList!=null&&n<nodeList.getLength();n++){
							if(nodeList.item(n) instanceof CharacterData)
								value=nodeList.item(n).getNodeValue();
								isRef=false;
						}
					}
					
					String type=((Element)nodes.item(i)).getAttribute("class");
					
					if(parserContext.getParser().isNodeEquals("key",(Element)ns.item(j))){
						k=new PropertyValue.Element(value,isRef,StringUtils.isBalank(type)?null:ClassUtils.forName(type));
					}else v=new PropertyValue.Element(value,isRef,StringUtils.isBalank(type)?null:ClassUtils.forName(type));
				}
				
				values.put(k,v);
			}
		}
		
		PropertyValue propertyValue=new PropertyValue.MapPropertyValue(values,e.getAttribute("class"));
		config.setValue(propertyValue);
		return config;
	}
	
	private Configuration parseCollectionConfig(Element e,ParserContext parserContext){
		Configuration config=new Configuration("java.util.Collection");
		
		if(e.hasAttribute("name")){
			config.setName(e.getAttribute("name"));
		}
		
		List<PropertyValue.Element> values=new ArrayList<PropertyValue.Element>();
		NodeList nodes=e.getChildNodes();
		for(int i=0;nodes!=null&&i<nodes.getLength();i++){
			Node node=nodes.item(i);
			if(node instanceof Element&&parserContext.getParser().isNodeEquals("value", (Element)node)){
			
				String value=((Element)nodes.item(i)).getAttribute("ref");
				boolean isRef=true;
				if(StringUtils.isBalank(value)){
					NodeList nodeList=node.getChildNodes();
					for(int j=0;nodeList!=null&&j<nodeList.getLength();j++){
						if(nodeList.item(j) instanceof CharacterData)
							value=nodeList.item(j).getNodeValue();
							isRef=false;
					}
				}
				
				String type=((Element)nodes.item(i)).getAttribute("class");
				
				values.add(new PropertyValue.Element(value,isRef,StringUtils.isBalank(type)?null:ClassUtils.forName(type)));
			}
		}
		
		PropertyValue propertyValue=new PropertyValue.CollectionPropertyValue(values, e.getAttribute("class"));
		config.setValue(propertyValue);
		return config;
	}
	
	private void constant(ParserContext parserContext,Configuration config) {
		parserContext.getReaderContext().getBuilder().factory(config.getClazz(), config.getName(), new UtilConfigFactory(config), Scope.SINGLETON);
	}
	
	static class UtilConfigFactory implements Factory<Object>{
		Configuration config;
		
		UtilConfigFactory(Configuration config){
			this.config=config;
		}
		
		@Override
		public Object create(Context context) {
			PropertyValue value=(PropertyValue) config.getValue();
			context.getContainer().inject(value);
			return value;
		}
		
	}

}
