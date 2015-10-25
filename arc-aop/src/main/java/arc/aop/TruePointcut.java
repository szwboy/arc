package arc.aop;

public class TruePointcut implements Pointcut {
	
	public static final Pointcut instance= new TruePointcut();
	
	private TruePointcut(){}

	@Override
	public ClassFilter getClassFilter() {
		return ClassFilter.TRUE;
	}

	@Override
	public MethodMatcher getMethodMatcher() {
		return MethodMatcher.TRUE;
	}

}
