package arc.ioc.xml;

import org.apache.commons.lang.ClassUtils;

public class Configuration {
	
	private Class<?> clazz;
	private String name;
	private Class<?> type;
	private String parent;
	private boolean isAbstract;
	private String scope;
	private Object value;
	private String initMethod;
	
	
	public String getInitMethod() {
		return initMethod;
	}

	public void setInitMethod(String initMethod) {
		this.initMethod = initMethod;
	}

	public Configuration(String className) throws ClassNotFoundException {
		this.clazz=ClassUtils.getClass(className);
	}
	
	public Class<?> getClazz() {
		return clazz;
	}

	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public boolean isAbstract() {
		return isAbstract;
	}

	public void setAbstract(boolean isAbstract) {
		this.isAbstract = isAbstract;
	}
	
	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

}
