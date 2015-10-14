package arc.core.bytecode;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtNewConstructor;
import javassist.CtNewMethod;
import javassist.NotFoundException;

public class ASMClassGenerator{
	private ClassPool mPool;
	private List<String> mMethods, mFields, mConstructors;
	private Set<String> ifcs;
	private String className;
	private boolean defaultConstructor;
	private AtomicLong CLASS_NAME_COUNTER= new AtomicLong(0);
	private CtClass ctClass;
	public final static String INIT_TAG= "<init>";
	private ClassReader cr;
	
	public static ASMClassGenerator newInstance(String file){
		
		return new ASMClassGenerator(file);
	}
	
	private ASMClassGenerator(String file){
		try {
			cr= new ClassReader(file);
		} catch (IOException e) {
		}
	}
	
	public void setSuperClass(String superClass){
		
		this.ctSc= superClass;
	}
	
	public void setClassName(String className){
		this.ctClassName= className;
	}
	
	public void addInterface(String ic){
		if(ifcs== null) ifcs= new HashSet<String>();
		
		ifcs.add(ic);
	}
	
	
	public void addMethod(String name, int mod, Class<?>[] pts, Class<?> rt, Class<?>[] ets, String body){
		
	}
	
	public void addField(String name, int mod, Class<?> pt, String def){
	}
	
	public void addConstructor(int mod, String body, Class<?>[] ets, Class<?>[] pts){
	}
	
	public void addConstructor(String constructor){
		if(mConstructors== null) mConstructors= new ArrayList<String>();
		mConstructors.add(constructor);
	}
	
	public Class<?> toCtClass(){
		try{
			if(ctClass!= null) ctClass.detach();
			
			CtClass ctSuperClass= ctSc== null? null: mPool.get(ctSc);
			if(ctClassName== null)
				ctClassName= (ctSc==null?
						JavassistClassGenerator.class.getName(): ctSc+"$sc")+ CLASS_NAME_COUNTER.getAndIncrement();
			
			ctClass= mPool.makeClass(ctClassName);
			if(ctSc!= null) ctClass.setSuperclass(ctSuperClass);
			
			if(ifcs!= null){
				
				for(String icf: ifcs){
					
					CtClass ctIfc= mPool.get(icf);
					ctClass.addInterface(ctIfc);
				}
			}
			ctClass.addInterface(mPool.get(DynamicInterface.class.getName()));
			
			if(mFields!= null){
				
				for(String mField: mFields){
					ctClass.addField(CtField.make(mField, ctClass));
				}
			}
			
			if(mMethods!= null){
				
				for(String mMethod: mMethods)
					ctClass.addMethod(CtNewMethod.make(mMethod, ctClass));
			}
			
			
			
			if(mConstructors!= null){
				
				for(String mConstructor: mConstructors){
					
					String[] sn= ctClass.getSimpleName().split("\\$+");
					ctClass.addConstructor(CtNewConstructor.make(mConstructor.replace("<init>", sn[sn.length-1]), ctClass));
				}
			}
			
			if(defaultConstructor) ctClass.addConstructor(CtNewConstructor.defaultConstructor(ctClass));
			
			return ctClass.toClass();
			
		}catch(RuntimeException e){
			
		} catch (NotFoundException e) {
		} catch (CannotCompileException e) {
		}
		
		return null;
	}
	
	/**
	 * release resources
	 */
	public void release(){
		
		if(ctClass!= null) ctClass.detach();
		if(mFields!=null) mFields.clear();
		if(mMethods!=null) mMethods.clear();
		if(this.ifcs!= null) ifcs.clear();
		if(this.mConstructors!=null) mConstructors.clear();
	}
	
	public void addDefaultConstructor(){
		this.defaultConstructor=true;
	}
	
	String deferModifier(int mod){
		
		if(Modifier.isPrivate(mod)) return "private";
		
		if(Modifier.isPublic(mod)) return "public";
		
		if(Modifier.isProtected(mod)) return "protected";
		
		return "";
	}
	
	interface DynamicInterface{}
}
