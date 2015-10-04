package arc.context.util;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.szw.ClassUtils;
import com.szw.ioc.BeanPostProcessor;
import com.szw.ioc.Container;
import com.szw.ioc.Inject;

/**
 * ���ڶ��ƻ�����util��value,��Ҫ���ڻص�
 * @author sunzhongwei
 *
 */
public abstract class PropertyValue{
	protected Container container;

	@Inject("container")
	public void setContainer(Container container) {
		this.container = container;
	}
	
	public static class CollectionPropertyValue extends PropertyValue implements BeanPostProcessor<Collection>{
		List<Element> eles;
		Class<?> cls;
		
		public CollectionPropertyValue(List<Element> eles,String cls){
			this.eles=eles;
			this.cls=ClassUtils.forName(cls);
		}

		@Override
		public Collection postProcessor() {
			if(eles==null||eles.size()==0) return null;
			
			Collection collection=null;
			try {
				collection = Collection.class.cast(cls.newInstance());
				for(Element ele:eles){
					collection.add(ele.convertValue(container));
				}

				return collection;
			} catch (InstantiationException e) {
			} catch (IllegalAccessException e) {
			}
			
			return collection;
		}
		
	}
	
	public static class Element{
		String value;
		boolean isRef;
		Class type;
		
		public Element(String value, boolean isRef, Class type) {
			this.value = value;
			this.isRef = isRef;
			this.type = type;
		}
		
		Object convertValue(Container container){
			if(isRef) return container.getInstance(value, type);
			
			return value;
			
		}
		
	}
	
	public static class ArrayPropertyValue<T> extends PropertyValue implements BeanPostProcessor<T[]>{
		Element[] eles;
		Class<T> c;
		
		public ArrayPropertyValue(Class<T> c,Element[] eles){
			this.eles=eles;
			this.c=c;
		}
		
		@Override
		public T[] postProcessor() {
			T[] ts=(T[]) Array.newInstance(c, eles.length);
			if(eles!=null&&eles.length>0){
				int i=0;
				for(Element ele:eles){
					Array.set(ts, i, ele.convertValue(container));
					i++;
				}
			}
			
			return ts;
		}
	}
	
	public static class MapPropertyValue extends PropertyValue implements BeanPostProcessor<Map>{
		
		Class<?> cls;
		Map<Element,Element> entries=new HashMap<Element,Element>();
		
		public MapPropertyValue(Map<Element,Element> entries,String cls) {
			this.entries=entries;
			this.cls=ClassUtils.forName(cls);;
		}

		@Override
		public Map postProcessor() {
			Map map=null;
			try {
				map=Map.class.cast(cls.newInstance());
				for(Entry<Element,Element> entry:entries.entrySet()){
					Element key=entry.getKey();
					Element value=entry.getValue();
					Object realKey=key.convertValue(container);
					Object realValue=value.convertValue(container);
					map.put(realKey, realValue);
				}
			} catch (InstantiationException e) {
			} catch (IllegalAccessException e) {
			}
			
			return map;
		}
		
		
	}
}
