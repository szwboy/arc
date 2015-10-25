package arc.aop;

public interface Pointcut {

	ClassFilter getClassFilter();
	
	MethodMatcher getMethodMatcher();
	
	Pointcut TRUE= TruePointcut.instance;
}
