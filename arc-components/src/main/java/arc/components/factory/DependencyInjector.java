package arc.components.factory;

/**
 * inject the dependency
 * @author sunzhongwei
 *
 */
public interface DependencyInjector {

	/**
	 * instantiate a bean
	 * @param type
	 * @return
	 */
	<T>T inject(final Class<T> type);
	
	/**
	 * inject the dependency
	 * @param t
	 */
	<T>void inject(T t);
}
