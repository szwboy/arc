package arc.core.util;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ClassUtils {

	/**
	 * get the most specified method for target class.
	 * @param originalMethod
	 * @param targetClass
	 * @return
	 */
	public static Method getMostSpecificMethod(Method originalMethod, Class<?> targetClass){
		if(originalMethod!= null&&isOverridable(originalMethod, targetClass)&&originalMethod.getDeclaringClass()!= targetClass){
			if(Modifier.isPublic(originalMethod.getModifiers())){
				
				try {
					return targetClass.getMethod(originalMethod.getName(), originalMethod.getParameterTypes());
				} catch (NoSuchMethodException | SecurityException e) {
					return originalMethod;
				}
			}else{
				return getMostSpecificMethod(originalMethod.getName(), originalMethod.getParameterTypes(), targetClass)== null? 
						originalMethod: getMostSpecificMethod(originalMethod.getName(), originalMethod.getParameterTypes(), targetClass);
			}
		}
		
		return originalMethod;
		
	}
	
	private static Method getMostSpecificMethod(String mn, Class<?>[] pts, Class<?> type){
		do{
			Class<?> clz= type;
			Method[] ms= type.getDeclaredMethods();
			for(Method m: ms){
				if(m.getName().equals(mn)&& Arrays.equals(pts, m.getParameterTypes())) return m;
			}
			
			clz= clz.getSuperclass(); 
		}while(type!= null&& type!= Object.class);
		
		return null;
	}
	
	/**
	 * whether or not the method can be overrided by the target class
	 * @param originalMethod
	 * @param targetClass
	 * @return
	 */
	private static boolean isOverridable(Method originalMethod, Class<?> targetClass){
		int mod=originalMethod.getModifiers();
		if(Modifier.isPrivate(mod)) return false;
		if(Modifier.isProtected(mod)|| Modifier.isPublic(mod)) return true;
		
		if(targetClass.getPackage().getName().equals(originalMethod.getDeclaringClass().getPackage().getName())) return true;
		
		return false;
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
	
	public static <T>List<Class<?>> getAllInterfacesForClass(Class<T> clz, ClassLoader cl){
		if(clz.isInterface()&& isVisible(clz, cl)){
			return Collections.<Class<?>>singletonList(clz);
		}
		
		List<Class<?>> result= new LinkedList<Class<?>>();
		do{
			Class<?>[] ifcs= clz.getInterfaces();
			for(Class<?> ifc: ifcs){
				result.addAll(getAllInterfacesForClass(ifc, cl));
			}
		}while(clz.getSuperclass()!= Object.class);
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static <T>boolean isVisible(Class<T> clz, ClassLoader cl){
		if(cl== null) return true;
		try {
			Class<T> actualClazz= (Class<T>) cl.loadClass(clz.getName());
			
			if(actualClazz== clz) return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
		
		return false;
	}
	
}
