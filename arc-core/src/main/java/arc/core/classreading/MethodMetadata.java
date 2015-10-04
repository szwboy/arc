package arc.core.classreading;

/**
 * method's meta data that means a method's description 
 * @author sunzhongwei
 *
 */
public interface MethodMetadata extends MemberMetadata{

	/**
	 * method name
	 * @return
	 */
	String getMethodName();
	
	/**
	 * whether is abstract
	 * @return
	 */
	boolean isAbstract();
	
	/**
	 * the class which the method is declared
	 * @return
	 */
	String getDeclaringClass();
	
}
