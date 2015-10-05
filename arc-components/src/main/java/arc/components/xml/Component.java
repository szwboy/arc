package arc.components.xml;

import org.apache.commons.lang.ClassUtils;

public class Component {
	private String id;
	private Class<?> type;
	private String scope;
	private Object value;

	public Component(String className) throws ClassNotFoundException {
		this.type=ClassUtils.getClass(className);
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public String getName() {
		return id;
	}

	public void setName(String id) {
		this.id = id;
	}

	public Class<?> getType() {
		return type;
	}

	public void setType(String type) throws ClassNotFoundException {
		this.type = ClassUtils.getClass(type);
	}
	
	public void setType(Class<?> type){
		this.type=type;
	}
	
	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

}
