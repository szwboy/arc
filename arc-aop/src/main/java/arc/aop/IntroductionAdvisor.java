package arc.aop;

public interface IntroductionAdvisor extends Advisor {

	ClassFilter getClassFilter();
	
	Class<?>[] getAllInterfaces();
}
