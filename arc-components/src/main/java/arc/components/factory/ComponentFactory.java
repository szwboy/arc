package arc.components.factory;


public interface ComponentFactory {
	/**
     * Default dependency name.
    */
    String DEFAULT_NAME = "default";

	/**
	 * get component instance by id and type
	 * @param name
	 * @param type
	 * @return
	 */
	<T>T getComponent(final String name, final Class<T> type);
	
}
