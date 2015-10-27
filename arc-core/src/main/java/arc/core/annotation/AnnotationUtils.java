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
	
	public static <A  extends Annotation>A findAnnotation(Class<?> clz, Class<A> annotationType, Set<Annotation> added){
		return null;
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
