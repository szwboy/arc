package arc.core.classreading;

import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * 
 * @author sunzhongwei
 * An annotation is also a class. On the other hand annotation is a specified class
 * so it extends classmetadata. 
 *
 */
public final class AnnotationMetadataReadingVisitor extends ClassMetadataReadingVisitor implements AnnotationMetadata{

	private Map<String, Map<String, Object>> attributes= new LinkedHashMap<String, Map<String, Object>>();
	private Set<MethodMetadata> methodMetadatas= new HashSet<MethodMetadata>();
	private Map<String, Set<String>> metaAnnotations= new LinkedHashMap<String, Set<String>>();
	
	public AnnotationMetadataReadingVisitor(ClassLoader classLoader) {
		super(classLoader);
	}
	
	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		
		if ((access & Opcodes.ACC_BRIDGE) != 0) {
			return super.visitMethod(access, name, desc, signature, exceptions);
		}
		
		boolean isFinal= Modifier.isFinal(access);
		boolean isAbstract= Modifier.isAbstract(access);
		boolean isStatic= Modifier.isStatic(access);
		MethodMetadataReadingVisitor methodMetadata= new MethodMetadataReadingVisitor(name, isFinal, isAbstract, isStatic, getName(), classLoader);
		methodMetadatas.add(methodMetadata);
		return methodMetadata;
	}
	
	public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
		return super.visitField(access, name, desc, signature, value);
	}
	
	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		String annotationType= Type.getType(desc).getClassName();
		return new AnnotationAttributesReadingVisitor(annotationType, attributes, metaAnnotations, classLoader);
	}

	@Override
	public Set<String> getMetaAnnotation(String annotationType) {
		return metaAnnotations.get(annotationType);
	}

	@Override
	public boolean hasMetaAnnotation(String annotationType) {
		return metaAnnotations.containsKey(annotationType);
	}

	@Override
	public Map<String, Object> getAnnotationAttributes(String annotationType) {
		return attributes.get(annotationType);
	}
	
	@Override
	public boolean hasAnnotatedMethod(String annotationType) {
		return methodMetadatas.contains(annotationType);
	}

	@Override
	public Set<MethodMetadata> getAnnotatedMethods(String annotationType) {
		Set<MethodMetadata> annotatedMethods = new LinkedHashSet<MethodMetadata>(4);
		for (MethodMetadata methodMetadata : this.methodMetadatas) {
			if (methodMetadata.isAnnotated(annotationType)) {
				annotatedMethods.add(methodMetadata);
			}
		}
		return annotatedMethods;
	}
	
	@Override
	public Set<String> getAnnotations() {
		return attributes.keySet();
	}

	@Override
	public boolean isAnnotated(String annotationType) {
		return attributes.containsKey(annotationType);
	}

	
}
