package arc.core.classreading;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * read metadata of a method
 */
public class MethodMetadataReadingVisitor extends MethodVisitor implements MethodMetadata{

	private String methodName;
	private boolean isFinal;
	private boolean isAbstract;
	private boolean isStatic;
	private String declaringClassName;
	private ClassLoader classLoader;
	
	private Map<String, Object> attributes= new LinkedHashMap<String, Object>();
	
	public MethodMetadataReadingVisitor(String methodName, boolean isFinal, boolean isAbstract,
						boolean isStatic, String declaringClassName, ClassLoader classLoader) {
		super(Opcodes.ASM5);
		this.methodName= methodName;
		this.isFinal= isFinal;
		this.isAbstract= isAbstract;
		this.isStatic= isStatic;
		this.declaringClassName= declaringClassName;
		this.classLoader= classLoader;
	}
	
	/**
	 * visit method annotation
	 */
	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		String annotationType= Type.getType(desc).getClassName();
		if(annotationType.startsWith("java.lang.annotation")) return super.visitAnnotation(desc, visible);
		
		return new AnnotationAttributesReadingVisitor(annotationType, attributes, null, classLoader);
	}
	
	/**
	 * visit the parameter's annotation
	 */
	public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible) {
		String annotationType= Type.getType(desc).getClassName();
		Parameter p= Parameter.getParameter(parameter, methodName);
		return new AnnotationAttributesReadingVisitor(annotationType, p.getAttributes(), null, classLoader);
	}

	/**
	 * visit the local variable
	 */
	public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
		if(!isStatic&& index==0){
			super.visitLocalVariable(name, desc, signature, start, end, index);
		}else{
			String pt= Type.getType(desc).getClassName();
			Parameter p= Parameter.getParameter(isStatic?index: index-1, methodName);
			p.setPt(pt);
			p.setPn(name);
		}
	}
	
	public void visitEnd() {
		
	}

	@Override
	public String getMethodName() {
		return methodName;
	}

	@Override
	public boolean isFinal() {
		return isFinal;
	}

	@Override
	public boolean isAbstract() {
		return isAbstract;
	}
	
	@Override
	public String getDeclaringClass() {
		return declaringClassName;
	}

	@Override
	public boolean isStatic() {
		return isStatic;
	}

	@Override
	public boolean isAnnotated(String annotationType) {
		return attributes.containsKey(annotationType);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> getAnnotationAttributes(String annotationType) {
		return (Map<String, Object>) attributes.get(annotationType);
	}
	
	static class Parameter{
		String pt;
		String pn;
		Map<String, Object> attributes;
		
		static Map<String, Parameter> parameters= new HashMap<String, Parameter>();
		
		static Parameter getParameter(int index, String mn){
			ParameterKey pk= new ParameterKey(mn, index);
			if(parameters.containsKey(pk)){
				return parameters.get(pk);
			}
			
			return new Parameter();
		}
		
		private Parameter(){
			attributes= new LinkedHashMap<String, Object>();
		}
		
		void setPt(String pt) {
			this.pt = pt;
		}

		void setPn(String pn) {
			this.pn = pn;
		}

		Map<String, Object> getAttributes() {
			return attributes;
		}
		
		public String getPt() {
			return pt;
		}
		
		public String getPn() {
			return pn;
		}
		
	}
	
	static class ParameterKey{
		String mn;
		int index;
		
		ParameterKey(String mn, int index){
			this.mn= mn;
			this.index= index;
		}

		@Override
		public int hashCode() {
			return mn.hashCode()^index;
		}

		@Override
		public boolean equals(Object obj) {
			if(!(obj instanceof ParameterKey)){
				return false;
			}
			
			if(obj== this) return true;
			ParameterKey pk= (ParameterKey)obj;
			if(pk.mn.equals(this.mn)&& pk.index== this.index) return true;
			
			return false;
		}
		
	}

}
