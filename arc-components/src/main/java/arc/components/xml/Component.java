package arc.components.xml;

import org.apache.commons.lang.ClassUtils;

public class Component<T> {
	private String id;
	private Class<T> type;
	private Class<? extends T> impl;
	private String scope;
	private Object value;

	public Component(Class<T> type){
		this.type=type;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Class<?> getImpl() {
		return impl;
	}

	@SuppressWarnings("unchecked")
	public void setImpl(String impl){
		
		try {
			this.impl = ClassUtils.getClass(impl);
		} catch (ClassNotFoundException e) {
			
		}
		if(type.isAssignableFrom(this.impl)) throw new IllegalArgumentException(impl +" should be the subclass of "+type.getCanonicalName());
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public Class<?> getType() {
		return type;
	}
	
	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

}
