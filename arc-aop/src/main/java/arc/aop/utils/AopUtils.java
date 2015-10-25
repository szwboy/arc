package arc.aop.utils;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import arc.aop.Advisor;
import arc.aop.IntroductionAdvisor;
import arc.aop.MethodMatcher;
import arc.aop.Pointcut;
import arc.aop.PointcutAdvisor;
import arc.aop.pointcut.IntroductionAwareMethodMatcher;
import arc.core.util.ClassUtils;
import arc.core.util.ReflectUtils;

public class AopUtils {

	public static List<Advisor> findAdvisorsCanApply(List<Advisor> candidates, Class<?> clz){
		
		List<Advisor> eligibleAdvisors= new LinkedList<Advisor>();
		for(Advisor candidate: candidates){
			if((candidate instanceof IntroductionAdvisor)&& canApply(candidate, clz)){
				eligibleAdvisors.add(candidate);
			}
		}
		
		boolean hasIntroduction= eligibleAdvisors.isEmpty()? false: true;
		for(Advisor candidate: candidates){
			if(candidate instanceof IntroductionAdvisor){
				continue;
			}
			
			if(canApply(candidate, clz, hasIntroduction)){
				eligibleAdvisors.add(candidate);
			}
		}
		
		return eligibleAdvisors;
	}
	
	public static boolean canApply(Advisor advisor, Class<?> clz){
		return canApply(advisor, clz, false);
	}
	
	public static boolean canApply(Advisor advisor, Class<?> clz, boolean hasIntroduction){
		if(advisor instanceof IntroductionAdvisor){
			return ((IntroductionAdvisor)advisor).getClassFilter().matches(clz);
		}
		
		if(advisor instanceof PointcutAdvisor){
			return canApply(((PointcutAdvisor)advisor).getPointcut(), clz, hasIntroduction);
		}
		
		//when it doesn't have a pointcut, we assume it true
		return true;
	}
	
	public static boolean canApply(Pointcut pointcut, Class<?> clz, boolean hasIntroduction){
		if(!pointcut.getClassFilter().matches(clz)) return false;
		
		MethodMatcher mm= pointcut.getMethodMatcher();
		IntroductionAwareMethodMatcher imm= null;
		if(mm instanceof IntroductionAwareMethodMatcher){
			imm=(IntroductionAwareMethodMatcher)mm;
		}
		
		List<Class<?>> allInterfaces= ClassUtils.getAllInterfacesForClass(clz, null);
		for(Class<?> ifc: allInterfaces){
			Method[] ms= ifc.getMethods();
			
			for(Method m: ms){
				if((imm!= null&& imm.matches(clz, m, hasIntroduction))|| mm.matches(m, clz)) return true;
			}
		}
		
		return false;
	}
}
