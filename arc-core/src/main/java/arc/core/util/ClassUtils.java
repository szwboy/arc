package arc.core.util;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

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
	
}
