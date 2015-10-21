package arc.core.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.LoaderClassPath;
import javassist.NotFoundException;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;

public class ReflectUtils {

	/**
	 * for example [[Ljava.lang.String; the result will be java.lang.String[][]
	 * @param c represents the class
	 * @return
	 */
	public static String getName(Class<?> c){
		
		StringBuilder sb= new StringBuilder();
		if(c.isArray()){
			sb.append("[]");
			do{
				c= c.getComponentType();
			}while(c.isArray());
		}
		
		return c.getName()+sb.toString();
	}
	
	/**
	 * for example int do(int i), the result will be do(I)I
	 * @param m represents the method
	 * @return
	 */
	public static String desc(Method m){
		
		StringBuilder sb= new StringBuilder();
		sb.append(m.getName()).append('(');
		
		Class<?>[] pts= m.getParameterTypes();
		if(pts!= null){
			
			for(Class<?> c: pts){
				sb.append(desc(c));
			}
		}
		
		Class<?> rt= m.getReturnType();
		sb.append(desc(rt));
		
		return sb.toString();
	}
	
	/**
	 * for example int[][], the result will be [[I;
	 * String[][], the result will be [[Ljava.lang.string;
	 * @param c represents Class
	 * @return
	 */
	public static String desc(Class<?> c){
		
		StringBuilder sb= new StringBuilder();
		
		while(c.isArray()){
			
			sb.append('[');
			c= c.getComponentType();
		}
		
		if(c.isPrimitive()){
			
			if(c== void.class) sb.append("V");
			if(c== short.class) sb.append("S");
			if(c== int.class) sb.append("I");
			if(c== long.class) sb.append("J");
			if(c== float.class) sb.append("F");
			if(c== double.class) sb.append("D");
			if(c== byte.class) sb.append("B");
			if(c== char.class) sb.append("C");
			if(c== boolean.class) sb.append("Z");
		}else{
			sb.append("L").append(c.getName().replaceAll(".", "/")).append(';');
		}
		
		return sb.toString();
	}
	
	/**
	 * detect the variable name
	 * @param c
	 * @param mn
	 * @param i
	 * @return
	 * @throws NotFoundException
	 */
	public static String detectPn(Class<?> c, String mn, int i) throws NotFoundException{
		ClassLoader cl= c.getClassLoader();
		ClassPool cp= new ClassPool(true);
		cp.appendClassPath(new LoaderClassPath(cl));
		CtClass ctc= cp.get(c.getName());
		
		CtMethod ctm= ctc.getDeclaredMethod(mn);
		MethodInfo mi= ctm.getMethodInfo();
		CodeAttribute ca= mi.getCodeAttribute();
		LocalVariableAttribute lva= (LocalVariableAttribute) ca.getAttribute(LocalVariableAttribute.tag);
		
		//since in the code table the first parameter is the this instance���we need add 1
		return lva.variableName(i+1);
	}
	
	public static String detectConstructorPn(Class<?> c, Class<?>[] pts , int i) throws NotFoundException{
		ClassLoader cl= c.getClassLoader();
		ClassPool cp= new ClassPool(true);
		cp.appendClassPath(new LoaderClassPath(cl));
		CtClass ctc= cp.get(c.getName());
		
		CtClass[] ctcs= new CtClass[pts.length];
		int j=0;
		for(Class<?> cc: pts){
			ctcs[j]=cp.get(cc.getName());
			j++;
		}
		CtConstructor ctcon= ctc.getDeclaredConstructor(ctcs);
		MethodInfo mi= ctcon.getMethodInfo();
		CodeAttribute ca= mi.getCodeAttribute();
		LocalVariableAttribute lva= (LocalVariableAttribute) ca.getAttribute(LocalVariableAttribute.tag);
		
		//since in the code table the first parameter is the this instance���we need add 1
		return lva.variableName(i+1);
	}
	
	@SuppressWarnings("unchecked")
	public static <T>Class<? super T>[] getAllInterfaces(Class<T> ifc){
		
		List<Class<? super T>> ret= new ArrayList<Class<? super T>>();
		Class<? super T> ic= ifc;
		do{
			ret.add(ic);
		}while((ic=ic.getSuperclass())!= null);
		
		return (Class<? super T>[]) ret.toArray(new Class<?>[0]);
	}
	
	public static String convertResourcePathToClassName(String className){
		return className.replace("/", ".");
	}
	
	@SuppressWarnings("rawtypes")
	public static Field findField(String enumType, String fieldName){

		Class enumClass;
		try {
			enumClass = Class.forName(enumType);
			do{
				Field[] fs= enumClass.getDeclaredFields();
				
				for(Field f: fs){
					String fn= f.getName();
					if(fn.equals(fn)){
						return f;
					}
				}
				enumClass= enumClass.getSuperclass();
			}while(enumClass!= null&& enumClass!= Object.class);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		
		return null;
	}
	
}
