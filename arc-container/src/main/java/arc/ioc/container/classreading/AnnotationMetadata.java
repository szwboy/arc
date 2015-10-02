package arc.ioc.container.classreading;

import java.util.Map;
import java.util.Set;

/**
 * annotation meta data in a class
 * @author sunzhongwei
 *
 */
public interface AnnotationMetadata extends ClassMetadata{

	/**
	 * annotations annotated above a class
	 * @return
	 */
	Set<String> getAnnotations();

	/**
	 * has some specified annotation
	 * @param annotationType
	 * @return
	 */
	boolean isAnnotated(String annotationType);
	
	/**
	 * annotation attributes for specified annottion
	 * @param annotationType
	 * @return
	 */
	Map<String, Object> getAnnotationAttributes(String annotationType);

	/**
	 * whether a method annotated by annotation
	 * @param annotationType
	 * @return
	 */
	boolean hasAnnotatedMethod(String annotationType);

	/**
	 * methods annotated by specified annotation
	 * @param annotationType
	 * @return
	 */
	Set<MethodMetadata> getAnnotatedMethods(String annotationType);
	
	/**
	 * annotations above a method
	 * @param annotationType
	 * @return
	 */
	Set<String> getMetaAnnotation(String annotationType);

	
	/**
	 * meta annotation means annotations annotated above an annotation class
	 * @param annotationType
	 * @return
	 */
	boolean hasMetaAnnotation(String annotationType);
	
	
}
