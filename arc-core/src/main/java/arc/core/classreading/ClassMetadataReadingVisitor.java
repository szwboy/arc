package arc.core.classreading;

import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

import com.sun.xml.internal.ws.org.objectweb.asm.Type;

import arc.core.util.ReflectUtils;

public class ClassMetadataReadingVisitor extends ClassVisitor implements ClassMetadata{
	private String className;//name of class
	private String enclosingClass;//outer class
	private String superClassName;//super class
	private String[] interfaces;//interface names
	private boolean isInterface;
	private boolean isAbstract;
	private boolean isFinal;
	private boolean isAnnotation;
	private Set<String> memberClasses;
	protected ClassLoader classLoader;
	
	public ClassMetadataReadingVisitor(ClassLoader classLoader){
		super(Opcodes.ASM5);
		this.classLoader= classLoader;
	}

	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		this.className= name.replace('/', '.');
		isAbstract= Modifier.isAbstract(access);
		isFinal= Modifier.isFinal(access);
		isAnnotation= (Opcodes.ACC_ANNOTATION& access)!= 0;
		this.interfaces= interfaces;
		this.superClassName= superName;
		isInterface= (Opcodes.ACC_INTERFACE& access)!= 0;
	}

	@Override
	public void visitOuterClass(String owner, String name, String desc) {
		enclosingClass= owner.replace("/", ".");
	}

	@Override
	public void visitInnerClass(String name, String outerName,String innerName, int access) {
		
		if(outerName!= null){
			String _outerName= ReflectUtils.convertResourcePathToClassName(outerName);
			String _name= ReflectUtils.convertResourcePathToClassName(name);
			if(_outerName.equals(this.className)){
				this.memberClasses.add(name);
			}else if(_name.equals(this.className)){
				enclosingClass= _outerName;
			}
		}
	}
	
	@Override
	public String getName() {
		return className;
	}

	@Override
	public boolean isAnnotation() {
		return isAnnotation;
	}

	@Override
	public boolean isAbstract() {
		return isAbstract;
	}

	@Override
	public boolean hasInterface() {
		return interfaces== null;
	}

	@Override
	public boolean hasSuperClass() {
		return superClassName== null;
	}

	@Override
	public String getSuperClass() {
		return superClassName;
	}

	@Override
	public String[] getInterfaces() {
		return interfaces;
	}

	@Override
	public boolean isFinal() {
		return isFinal;
	}

	@Override
	public boolean isConcrete() {
		return !(isAbstract|| isInterface);
	}

	@Override
	public String getEnclosingClass() {
		return enclosingClass;
	}

	@Override
	public String[] getMemberClasses() {
		return memberClasses.toArray(new String[0]);
	}
	
}
