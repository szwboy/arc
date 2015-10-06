package arc.components.factory;

import java.util.Set;

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
	
	/**
	 * get component instance by default id and type
	 * @param type
	 * @return
	 */
	<T>T getComponent(Class<T> type);
	
	/**
	 * get all component names by type
	 * @param type
	 * @return
	 */
	<T>Set<String> getComponentNames(Class<T> type);
	
	/**
	 * instantiate a bean
	 * @param type
	 * @return
	 */
	<T> T inject(final Class<T> type);
}
