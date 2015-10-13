package arc.core.bytecode;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang.StringUtils;

import arc.core.util.ReflectUtils;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtNewConstructor;
import javassist.CtNewMethod;
import javassist.LoaderClassPath;
import javassist.NotFoundException;

public class ClassGenerator {

	private static Map<ClassLoader, ClassPool> CLASS_POOLS;
	
	private ClassPool mPool;
	
	private List<String> mMethods, mFields, mConstructors;
	private Set<String> ifcs;
	private String ctSc, ctClassName;
	private boolean defaultConstructor;
	
	private AtomicLong CLASS_NAME_COUNTER= new AtomicLong(0);
	
	private CtClass ctClass;
	
	private final static String INIT_TAG= "<init>";
	
	private static ClassPool getClassPool(ClassLoader cl){
		
		if(CLASS_POOLS== null) CLASS_POOLS= new HashMap<ClassLoader, ClassPool>();
		ClassPool cp= CLASS_POOLS.get(cl);
		
		if(cp==null){
			
			cp= new ClassPool(true);
			cp.appendClassPath(new LoaderClassPath(cl));
			CLASS_POOLS.put(cl, cp);
		}
		
		return cp;
	}
	
	public static ClassGenerator newInstance(ClassLoader cl){
		
		return new ClassGenerator(getClassPool(cl));
	}
	
	private ClassGenerator(ClassPool cp){
		
		this.mPool= cp;
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
		
		StringBuilder sb= new StringBuilder();
		
		//method name
		sb.append(deferModifier(mod)).append(' ').append(ReflectUtils.getName(rt)).append(' ').append(name).append('(');
		
		//parameter
		if(pts!=null){
			
			int i=0;
			for(Class<?> pt: pts){
				
				i++;
				sb.append(ReflectUtils.getName(pt)).append(" arg").append(i);
				
				if(i!= pts.length) sb.append(',');
			}
		}
		
		sb.append(')');
		
		if(ets.length> 0){
			
			sb.append("throws ");
			int i=0;
			for(Class<?> et: ets){
				
				i++;
				sb.append(ReflectUtils.getName(et));
				
				if(i!= ets.length) sb.append(',');
			}
		}
		
		sb.append('{').append(body).append('}');
		
		addMethod(sb.toString());
	}
	
	public void addMethod(String method){
		
		if(mMethods== null) mMethods= new ArrayList<String>();
		mMethods.add(method);
	}
	
	public void addField(String name, int mod, Class<?> pt, String def){
		
		StringBuilder sb= new StringBuilder();
		
		sb.append(deferModifier(mod)).append(' ').append(ReflectUtils.getName(pt)).append(' ').append(name);
		
		if(StringUtils.isNotBlank(def)) sb.append('=').append(def).append(';');
	}
	
	public void addField(String field){
		if(mFields== null) mFields= new ArrayList<String>();
		mFields.add(field);
	}
	
	public void addConstructor(int mod, String body, Class<?>[] ets, Class<?>[] pts){
		
		StringBuilder sb= new StringBuilder();
		//method name
		sb.append(deferModifier(mod)).append(' ').append(INIT_TAG).append('(');
		
		//parameter
		if(pts!=null){
			
			int i=0;
			for(Class<?> pt: pts){
				
				i++;
				sb.append(ReflectUtils.getName(pt)).append(" arg").append(i);
				
				if(i!= pts.length) sb.append(',');
			}
		}
		
		sb.append(')');
		
		if(ets.length> 0){
			
			sb.append("throws ");
			int i=0;
			for(Class<?> et: ets){
				
				i++;
				sb.append(ReflectUtils.getName(et));
				
				if(i!= ets.length) sb.append(',');
			}
		}
		
		sb.append('{').append(body).append('}');
		
		addConstructor(sb.toString());
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
						ClassGenerator.class.getName(): ctSc+"$sc")+ CLASS_NAME_COUNTER.getAndIncrement();
			
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
