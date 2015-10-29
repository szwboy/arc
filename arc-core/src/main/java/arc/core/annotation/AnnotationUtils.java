package arc.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class AnnotationUtils {

	private static final Map<AnnotationCacheKey, Annotation> findAnnotationCache=
			new ConcurrentHashMap<AnnotationCacheKey, Annotation>();
	
	/**
	 * according to the cache key, find the annotation.
	 * @param clz
	 * @param annotationType
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <A  extends Annotation>A findAnnotation(Class<?> clz, Class<A> annotationType){
		AnnotationCacheKey annotationCacheKey= new AnnotationCacheKey(clz, annotationType);
		A result= (A) findAnnotationCache.get(annotationCacheKey);
		
		if(result== null){
			result= findAnnotation(clz, annotationType, new HashSet<Annotation>());
			if(result!= null){
				findAnnotationCache.put(annotationCacheKey, result);
			}
		}
		
		return result;
	}
	
	/**
	 * find annotation from class
	 * @param clz
	 * @param annotationType
	 * @param added
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <A  extends Annotation>A findAnnotation(Class<?> clz, Class<A> annotationType, Set<Annotation> added){
		Annotation[] anns= clz.getAnnotations();
		for(Annotation ann: anns){
			if(ann.annotationType().equals(annotationType)){
				return (A) ann;
			}
		}
		
		//find annotation above the annotation 
		for(Annotation ann: anns){
			if(!isInJavaLangAnnotationPackage(ann)&& added.add(ann)){
				A a= findAnnotation(ann.annotationType(), annotationType, added);
				if(a!= null){
					return a;
				}
			}
		}
		
		//find annotation above interfaces
		Class<?>[] interfaces= clz.getInterfaces();
		for(Class<?> ifc: interfaces){
			A a= findAnnotation(ifc, annotationType, added);
			if(a!= null){
				return a;
			}
		}
		
		//find annotation above all super classes
		Class<?> superClass= clz.getSuperclass();
		while(superClass== null|| superClass== Object.class){
			return null;
		}
		
		return findAnnotation(superClass, annotationType, added);
	}
	
	public static boolean isInJavaLangAnnotationPackage(Annotation ann){
		return isInJavaLangAnnotationPackage(ann.annotationType().getName());
	}
	
	public static boolean isInJavaLangAnnotationPackage(String name){
		return name.startsWith("java.lang.annotation");
	}
	
	private static class AnnotationCacheKey{
		AnnotatedElement annotatedElement;
		Class<?> annotationType;
		
		AnnotationCacheKey(AnnotatedElement annotatedElement, Class<?> annotationType){
			this.annotatedElement= annotatedElement;
			this.annotationType= annotationType;
		}
		
		public boolean equals(Object other){
			if (this == other) {
				return true;
			}
			if (!(other instanceof AnnotationCacheKey)) {
				return false;
			}
			AnnotationCacheKey otherKey = (AnnotationCacheKey) other;
			return this.annotatedElement.equals(otherKey.annotatedElement) &&
					this.annotationType== otherKey.annotationType;
		}
		
		public int hashCode(){
			return (this.annotatedElement.hashCode() * 29 + this.annotationType.hashCode());
		}
	}
	
}
