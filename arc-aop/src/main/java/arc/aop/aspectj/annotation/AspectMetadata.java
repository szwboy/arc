package arc.aop.aspectj.annotation;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.AjType;
import org.aspectj.lang.reflect.AjTypeSystem;
import org.aspectj.lang.reflect.PerClauseKind;

import arc.aop.ClassFilter;
import arc.aop.MethodMatcher;
import arc.aop.Pointcut;
import arc.aop.aspectj.AspectjExpressionPointcut;
import arc.aop.aspectj.TypePatternClassFilter;

public class AspectMetadata {

	private final AjType<?> ajType;
	
	private final Pointcut perClausePointcut;
	
	private String aspectName;
	
	public AspectMetadata(final Class<?> clz, String aspectName){
		this.aspectName= aspectName;
		
		Class<?> curClass= clz;
		AjType<?> ajType= null;
		do{
			ajType= AjTypeSystem.getAjType(curClass);
			if(ajType.isAspect()){
				break;
			}
		}while(curClass!= Object.class);
		
		if (ajType == null) {
			throw new IllegalArgumentException("Class '" + clz.getName() + "' is not an @AspectJ aspect");
		}
		this.ajType= ajType;
		PerClauseKind perClauseKind= ajType.getPerClause().getKind();
		switch(perClauseKind){
			case SINGLETON: 
				perClausePointcut= Pointcut.TRUE;
				break;
			
			case PERTHIS: case PERTARGET:
				AspectjExpressionPointcut ajexp= new AspectjExpressionPointcut();
				ajexp.setExpression(findPerClause(clz));
				perClausePointcut= ajexp;
				break;
				
			case PERTYPEWITHIN:
				perClausePointcut= new Pointcut(){

					@Override
					public ClassFilter getClassFilter() {
						return new TypePatternClassFilter(findPerClause(clz));
					}

					@Override
					public MethodMatcher getMethodMatcher() {
						return null;
					}
					
				};
			default:
				throw new IllegalArgumentException("PerClause "+ajType.getName()+" not support by arc aop");
				
		}
	}
	
	/**
	 * Extract contents from String of form {@code pertarget(contents)}.
	 */
	private String findPerClause(Class<?> aspectClass) {
		// TODO when AspectJ provides this, we can remove this hack. Hence we don't
		// bother to make it elegant. Or efficient. Or robust :-)
		String str = aspectClass.getAnnotation(Aspect.class).value();
		str = str.substring(str.indexOf("(") + 1);
		str = str.substring(0, str.length() - 1);
		return str;
	}
	
	public Pointcut getPerClausePointcut(){
		return perClausePointcut;
	}
	
	public Class<?> getAspectClass(){
		return ajType.getJavaClass();
	}
	
	public String getAspectName(){
		return aspectName;
	}
	
	public boolean isLazilyInstantiated(){
		PerClauseKind kind= ajType.getPerClause().getKind();
		return kind== PerClauseKind.PERTHIS|| kind== PerClauseKind.PERTARGET|| kind== PerClauseKind.PERTYPEWITHIN;
	}
}
