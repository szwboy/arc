package arc.components.factory;

public interface ComponentPostProcessor<T> {

	T posProcessorBeforeInitialization(T t, String beanName);
	
	T postProcessorAfterInitialization(T t, String beanName);
}
