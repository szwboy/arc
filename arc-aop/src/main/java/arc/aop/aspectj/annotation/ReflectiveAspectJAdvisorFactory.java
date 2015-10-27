package arc.aop.aspectj.annotation;

import java.lang.reflect.Method;
import java.util.List;

import org.aopalliance.aop.Advice;

import arc.aop.Advisor;
import arc.aop.aspectj.AspectjExpressionPointcut;

public class ReflectiveAspectJAdvisorFactory implements AspectJAdvisorFactory{

	@Override
	public boolean isAspect(Class<?> clazz) {
		
	}

	@Override
	public List<Advisor> getAdvisors(MetadataAwareAspectInstanceFactory aif) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Advisor getAdvisor(Method candidateAdviceMethod,
			MetadataAwareAspectInstanceFactory aif,
			int declarationOrderInAspect, String aspectName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Advice getAdvice(Method candidateAdviceMethod,
			MetadataAwareAspectInstanceFactory aif,
			AspectjExpressionPointcut pointcut, int declarationOrderInAspect,
			String aspectName) {
		// TODO Auto-generated method stub
		return null;
	}

}
