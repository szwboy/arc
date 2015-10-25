package arc.aop;

import java.lang.reflect.Method;

public class TrueMethodMatcher implements MethodMatcher {
	public static final MethodMatcher TRUE= new TrueMethodMatcher();
	
	private TrueMethodMatcher(){}

	@Override
	public boolean matches(Method method, Class<?> clz) {
		return true;
	}

	@Override
	public boolean matches(Method method, Class<?> clz, Object[] args) {
		return true;
	}

}
