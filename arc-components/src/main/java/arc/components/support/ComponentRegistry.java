package arc.components.support;


public interface ComponentRegistry {

	/**
	 * factory to generate bean component
	 */
	<T>void factory(String name, final Class<T> impl, Scope scope);
	
	/**
	 * factory to generate constant
	 * @param name
	 * @param value
	 * @param type
	 */
	<T>void constant(String name, String value, Class<T> type, Class<? extends T> impl);
}
