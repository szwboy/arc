package arc.aop.aspectj.annotation;

import java.lang.reflect.Method;
import java.util.List;

import org.aopalliance.aop.Advice;

import arc.aop.Advisor;
import arc.aop.aspectj.AspectjExpressionPointcut;

public interface AspectJAdvisorFactory {

	boolean isAspect(Class<?> clazz);
	
	List<Advisor> getAdvisors(MetadataAwareAspectInstanceFactory aif);
	
	Advisor getAdvisor(Method candidateAdviceMethod, MetadataAwareAspectInstanceFactory aif,
			int declarationOrderInAspect, String aspectName);
	
	Advice getAdvice(Method candidateAdviceMethod, MetadataAwareAspectInstanceFactory aif,
			AspectjExpressionPointcut pointcut, int declarationOrderInAspect, String aspectName);
}
