package arc.core.classreading;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ReflectPermission;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import arc.common.utils.ReflectUtils;

class RecursiveAnnotationVisitor extends AnnotationVisitor {

	protected Map<String, Object> attributes;
	protected ClassLoader classLoader;
	
	public RecursiveAnnotationVisitor(Map<String, Object> attributes, ClassLoader classLoader){
		super(Opcodes.ASM5);
		this.attributes= attributes;
		this.classLoader= classLoader;
	}

	@Override
	public void visit(String name, Object value) {
		attributes.put(name, value);
	}

	@Override
	public AnnotationVisitor visitAnnotation(String name, String desc) {
		Map<String, Object> nestedAttributes= new LinkedHashMap<String, Object>();
		attributes.put(name, nestedAttributes);
		return new RecursiveAnnotationVisitor(nestedAttributes, classLoader);
	}

	@Override
	public AnnotationVisitor visitArray(String name) {
		Map<String, Object> nestedAttributes= new LinkedHashMap<String, Object>();
		return new RecursiveAnnoationArrayVisitor(name, nestedAttributes, this.classLoader);
	}

	@Override
	public void visitEnum(String name, String desc, String value) {
		String enumType= Type.getType(desc).getClassName();
		Field field= ReflectUtils.findField(enumType, value);
		if(field!= null){
			try {
				if(field.isAccessible()){
					SecurityManager sm= System.getSecurityManager();
					sm.checkPermission(new ReflectPermission("suppressCheck"));
					field.setAccessible(true);
				}
				Object val= field.get(null);
				
				visit(name, val);
			} catch (IllegalArgumentException e) {
			} catch (IllegalAccessException e) {
			}
			
		}
			
	}
	
}

public class AnnotationAttributesReadingVisitor extends RecursiveAnnotationVisitor{

	private Map<String, Map<String, Object>> attributesMap;
	private String annotationType;
	private Map<String, Set<String>> metaAnnotations;
	
	AnnotationAttributesReadingVisitor(String annotationType, Map<String, Map<String, Object>> attributes, Map<String, Set<String>> metaAnnotations, ClassLoader classLoader) {
		super(new LinkedHashMap<String, Object>(), classLoader);
		this.attributesMap= attributes;
		this.annotationType= annotationType;
		this.metaAnnotations= metaAnnotations;
	}

	@Override
	public void visitEnd() {
		attributesMap.put(annotationType, attributes);
		
		if(metaAnnotations!= null)
			doVisitEnd();
	}
	
	void doVisitEnd(){
		try {
			Class<?> annotationClass= classLoader.loadClass(annotationType);
			Annotation[] anns= annotationClass.getAnnotations();
			
			Set<String> annotationTypeNames= new HashSet<String>(anns.length);
			for(Annotation ann: anns){
				String annName= ann.annotationType().getName();
				if(annName.startsWith("java.lang.annotation")) continue;
				
				annotationTypeNames.add(ann.annotationType().getName());
			}
			
			metaAnnotations.put(annotationType, annotationTypeNames);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
	
	public Map<String, Set<String>> getMetaAnnotations(){
		return metaAnnotations;
	}
}

class RecursiveAnnoationArrayVisitor extends RecursiveAnnotationVisitor{

	private String name;
	private List<Map<String, Object>> nestedAttributes= new LinkedList<Map<String, Object>>();
	
	public RecursiveAnnoationArrayVisitor(String name, Map<String, Object> attributes, ClassLoader classLoader) {
		super(attributes, classLoader);
	}

	public void visit(String name, Object value) {
		Object[] values= null;
		if(attributes.containsKey(name)){
			values= (Object[]) attributes.get(name);
			values= Arrays.copyOf(values, values.length+1);
			values[values.length]= value;
		}else{
			values= new Object[1];
			values[0]= value;
		}
		
		attributes.put(name, values);
	}

	@Override
	public AnnotationVisitor visitAnnotation(String name, String desc) {
		Map<String, Object> attributes= new LinkedHashMap<String, Object>();
		nestedAttributes.add(attributes);
		return new RecursiveAnnotationVisitor(attributes, classLoader);
	}

	@Override
	public void visitEnd() {
		if(!nestedAttributes.isEmpty()){
			this.attributes.put(name, nestedAttributes.toArray(new Map[0]));
		}
	}
	
}
