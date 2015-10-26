package arc.aop.aspectj;

public interface AspectInstanceFactory {

	Object getAspectInstance();
	
	ClassLoader getAspectClassLoader();
}
