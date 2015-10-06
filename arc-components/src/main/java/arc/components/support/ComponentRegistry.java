package arc.components.support;

public interface ComponentRegistry {

	/**
	 * factory to generate bean component
	 */
	<T>void factory(String name, final Class<T> type, final Class<? extends T> impl);
	
	/**
	 * factory to generate constant
	 * @param name
	 * @param value
	 * @param type
	 */
	<T>void constant(String name, String value, Class<T> type);
	
	/**
	 * whether or not contains the named components
	 * @param name
	 * @return
	 */
	<T>boolean containesComponent(String name, Class<T> type);
}
