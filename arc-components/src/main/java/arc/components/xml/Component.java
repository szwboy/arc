package arc.components.xml;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Element;

import arc.components.support.Scope;

public class Component<T> {
	private Class<? extends T> impl;
	private Class<T> type;
	private Scope scope= Scope.Default;
	private String value;
	private String id;
	
	
	public Component(Class<T> type){
		this.type= type;
	}
	
	public Class<T> getType() {
		return type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setImpl(Class<? extends T> impl) {
		this.impl = impl;
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

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
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
	
	void accept(ComponentVisitor visitor){
		visitor.visit(this);
	}
	
}

final class ComponentAttributeVisitor implements ComponentVisitor{

	private Element e;
	
	ComponentAttributeVisitor(Element e){
		this.e= e;
	}
	
	@Override
	public <T>void visit(Component<T> component) {
		String id= e.getAttribute(ComponentConfigParserDelegate.ID_ATTRIBUTE);
		if(StringUtils.isNotBlank(id)){
			component.setId(id);
		}
		
		String scope= e.getAttribute(ComponentConfigParserDelegate.SCOPE_ATTRIBUTE);
		if(StringUtils.isNotBlank(scope)){
			component.setScope(scope);
		}
	}
	
}

final class ConstantVisitor implements ComponentVisitor{

	private Element e;
	
	ConstantVisitor(Element e){
		this.e= e;
	}
	
	@Override
	public <T>void visit(Component<T> constant) {
		String id= e.getAttribute(ComponentConfigParserDelegate.ID_ATTRIBUTE);
		if(StringUtils.isNotBlank(id)){
			constant.setId(id);
		}
		
		String impl= e.getAttribute(ComponentConfigParserDelegate.IMPL_ATTRIBUTE);
		if(StringUtils.isNotBlank(impl)){
			constant.setImpl(impl);
		}
		
		String value= e.getAttribute(ComponentConfigParserDelegate.VALUE_ATTRIBUTE);
		if(StringUtils.isNotBlank(value)){
			constant.setValue(value);
		}
		
		constant.setScope("singleton");
	}
	
}
