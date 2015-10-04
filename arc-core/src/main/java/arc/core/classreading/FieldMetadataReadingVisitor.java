package arc.core.classreading;

import java.util.LinkedHashMap;
import java.util.Map;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class FieldMetadataReadingVisitor extends FieldVisitor implements FieldMetadata{

	private boolean isStatic;
	private boolean isFinal;
	private String type;
	private String name;
	private ClassLoader classLoader;
	
	private Map<String, Map<String, Object>> attributes;

	public FieldMetadataReadingVisitor(int access, String name, String value, String desc, ClassLoader classLoader) {
		super(Opcodes.ASM5);
		isStatic= (access& Opcodes.ACC_STATIC)!= 0;
		isFinal= (access&Opcodes.ACC_FINAL)!= 0;
		type= Type.getType(desc).getClassName();
		this.name= name;
		this.classLoader= classLoader;
		attributes= new LinkedHashMap<String, Map<String, Object>>();
	}

	@Override
	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		String annotationType= Type.getType(desc).getClassName();
		return new AnnotationAttributesReadingVisitor(annotationType, attributes, null, classLoader);
	}

	public boolean isStatic() {
		return isStatic;
	}

	@Override
	public boolean isFinal() {
		return isFinal;
	}

	@Override
	public boolean isAnnotated(String annotationType) {
		return false;
	}

	@Override
	public Map<String, Object> getAnnotationAttributes(String annotationType) {
		return null;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public String getName() {
		return name;
	}

}
