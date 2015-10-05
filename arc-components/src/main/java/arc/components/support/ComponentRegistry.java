package arc.components.support;

public interface ComponentRegistry {

	<T>void factory(String name, final Class<T> type, final Class<? extends T> impl);
	
}
