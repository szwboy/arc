package arc.ioc.container.classreading;

import java.util.Set;

/**
 * meta data of a class
 * @author sunzhongwei
 *
 */
public interface ClassMetadata {

	/**
	 * whether or not an annotation
	 * @return
	 */
	boolean isAnnotation();
	
	/**
	 * whether or not abstract
	 * @return
	 */
	boolean isAbstract();
	
	/**
	 * whether or not has interfaces
	 * @return
	 */
	boolean hasInterface();
	
	/**
	 * whether or not has super class
	 * @return
	 */
	boolean hasSuperClass();
	
	/**
	 * super class of a class
	 * @return
	 */
	String getSuperClass();
	
	/**
	 * interfaces of a class
	 * @return
	 */
	String[] getInterfaces();
	
	/**
	 * the name of class
	 * @return
	 */
	String getName();
	
	/**
	 * whether or not a final class
	 * @return
	 */
	boolean isFinal();
	
	/**
	 * concrete class means neither abstract nor interface
	 * @return
	 */
	boolean isConcrete();
	
	/**
	 * enclosing class of a inner class
	 * @return
	 */
	String getEnclosingClass();
	
	/**
	 * member classes of a class
	 * @return
	 */
	String[] getMemberClasses();
	
}
