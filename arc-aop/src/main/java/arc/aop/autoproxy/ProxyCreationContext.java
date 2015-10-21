package arc.aop.autoproxy;


public class ProxyCreationContext {

	private static final ThreadLocal<String> currentProxiedBeanName= new ThreadLocal<String>();
	
	/**
	 * Return the name of the currently proxied bean instance.
	 * @return the name of the bean, or {@code null} if none available
	 */
	public static String getCurrentProxiedBeanName() {
		return currentProxiedBeanName.get();
	}

	/**
	 * Set the name of the currently proxied bean instance.
	 * @param beanName the name of the bean, or {@code null} to reset it
	 */
	static void setCurrentProxiedBeanName(String beanName) {
		if (beanName != null) {
			currentProxiedBeanName.set(beanName);
		}
		else {
			currentProxiedBeanName.remove();
		}
	}
}
