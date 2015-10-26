package arc.components.factory;


public interface ComponentFactory {

	/**
	 * get component instance by id and type
	 * @param name
	 * @param type
	 * @return
	 */
	<T>T getComponent(final String name, final Class<T> type);
	
	/**
	 * get component instance by id and type
	 * @param name
	 * @return
	 */
	Object getComponent(final String name);
	
}
