package arc.aop.aspectj.annotation;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import org.aopalliance.aop.Advice;
import org.aspectj.lang.annotation.Aspect;

import arc.aop.Advisor;
import arc.aop.Pointcut;
import arc.aop.aspectj.AspectjExpressionPointcut;
import arc.core.annotation.AnnotationUtils;
import arc.core.util.ReflectionUtils;

public class ReflectiveAspectJAdvisorFactory implements AspectJAdvisorFactory{

	@Override
	public boolean isAspect(Class<?> clazz) {
		if(AnnotationUtils.findAnnotation(clazz, Aspect.class)!= null) return true;
		
		return false;
	}

	@Override
	public List<Advisor> getAdvisors(MetadataAwareAspectInstanceFactory aif) {
		return null;
	}
	
	private List<Method> getAdvisorMethod(Class<?> clazz){
		final List<Method> result= new LinkedList<Method>();
		ReflectionUtils.doWithMethods(clazz, new ReflectionUtils.MethodCallback() {
			@Override
			public void doWith(Method method) {
				if(AnnotationUtils.findAnnotation(method, Pointcut.class)== null){
					result.add(method);
				}
			}
		});
		
		return result;
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
