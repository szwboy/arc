package arc.components.xml;

import org.apache.commons.lang.ClassUtils;
import arc.components.support.Scope;

public class Component<T> {
	private Class<T> impl;
	private Scope scope= Scope.Default;
	private Object value;
	private String id;
	
	public Component(Class<T> impl){
		this.impl= impl;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Class<? extends T> getImpl() {
		return impl;
	}

	@SuppressWarnings("unchecked")
	public void setImpl(String impl){
		
		try {
			this.impl = ClassUtils.getClass(impl);
		} catch (ClassNotFoundException e) {
			
		}
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
	
	public Scope getScope() {
		return scope;
	}

	public void setScope(String scope) {
		switch(scope){
			case "singleton":
				this.scope= Scope.Singleton;
				break;
			case "thread":
				this.scope= Scope.Thread;
				break;
		}
	}

}
