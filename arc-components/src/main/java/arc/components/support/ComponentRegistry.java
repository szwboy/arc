package arc.components.support;

import java.util.Set;


public interface ComponentRegistry {

	/**
	 * factory to generate bean component
	 */
	<T>void factory(String name, final Class<T> impl, Scope scope);
	
	/**
	 * factory to generate bean component
	 */
	<T>void factory(String name, final Class<T> impl, T t, Scope scope);
	/**
	 * factory to generate constant
	 * @param name
	 * @param value
	 * @param type
	 */
	<T>void constant(String name, String value, Class<? super T> type, Class<T> impl);
	
	/**
	 * containes specified factory
	 * @param name
	 * @param requiredType
	 * @return
	 */
	<T>boolean containesFactory(String name);
	
	/**
	 * get all component names by type
	 * @param type
	 * @return
	 */
	Set<String> getComponentNames(Class<?> type);
	
	/**
	 * get the bean type
	 * @param name
	 * @return
	 */
	Class<?> getType(String name);
	
	/**
	 * if the component is singleton
	 * @param name
	 * @return
	 */
	boolean isSingleton(String name);
	
	/**
	 * if the component is prototype
	 * @param name
	 * @return
	 */
	boolean isPrototype(String name);
}
