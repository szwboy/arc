package arc.aop.aspectj;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.aopalliance.intercept.MethodInvocation;
import org.aspectj.weaver.patterns.NamePattern;
import org.aspectj.weaver.reflect.ShadowMatchImpl;
import org.aspectj.weaver.tools.ContextBasedMatcher;
import org.aspectj.weaver.tools.FuzzyBoolean;
import org.aspectj.weaver.tools.JoinPointMatch;
import org.aspectj.weaver.tools.MatchingContext;
import org.aspectj.weaver.tools.PointcutDesignatorHandler;
import org.aspectj.weaver.tools.PointcutExpression;
import org.aspectj.weaver.tools.PointcutParameter;
import org.aspectj.weaver.tools.PointcutParser;
import org.aspectj.weaver.tools.PointcutPrimitive;
import org.aspectj.weaver.tools.ShadowMatch;

import arc.aop.ClassFilter;
import arc.aop.MethodMatcher;
import arc.aop.ProxyMethodInvocation;
import arc.aop.autoproxy.ProxyCreationContext;
import arc.aop.interceptor.ExposedInvocationInterceptor;
import arc.aop.pointcut.ExpressionPointcut;
import arc.aop.pointcut.IntroductionAwareMethodMatcher;
import arc.core.util.ClassUtils;

/**
 * 
 * @author sunzhongwei
 *
 */
public class AspectjExpressionPointcut implements ExpressionPointcut, ClassFilter, IntroductionAwareMethodMatcher {
	private ClassLoader componentClassLoader;
	private Set<PointcutPrimitive> supportedPointcutKinds= new HashSet<PointcutPrimitive>();
	
	private Class<?> pointcutDeclarationScope;
	private Class<?>[] pointcutParameterTypes= new Class<?>[0];
	private String[] pointcutParameterNames= new String[0];
	
	private PointcutExpression pointcutExpression;
	private String expression;
	private Map<Method, ShadowMatch> shadowMatchCache= new HashMap<Method, ShadowMatch>();
	
	/**
	 * init the pointcut kinds which this pointcut supports
	 */
	private void initPointcutPrimitive(){
		supportedPointcutKinds.add(PointcutPrimitive.EXECUTION);
		supportedPointcutKinds.add(PointcutPrimitive.ARGS);
		supportedPointcutKinds.add(PointcutPrimitive.REFERENCE);
		supportedPointcutKinds.add(PointcutPrimitive.THIS);
		supportedPointcutKinds.add(PointcutPrimitive.WITHIN);
		supportedPointcutKinds.add(PointcutPrimitive.TARGET);
		supportedPointcutKinds.add(PointcutPrimitive.AT_ANNOTATION);
		supportedPointcutKinds.add(PointcutPrimitive.AT_WITHIN);
		supportedPointcutKinds.add(PointcutPrimitive.AT_ARGS);
		supportedPointcutKinds.add(PointcutPrimitive.AT_TARGET);
	}
	
	public AspectjExpressionPointcut(){}
	
	public AspectjExpressionPointcut(Class<?> pointcutDeclarationScope, String[] paramNames, Class<?>[] paramTypes){
		initPointcutPrimitive();
		this.pointcutParameterTypes= paramTypes;
		this.pointcutParameterNames= paramNames;
	}

	public void setExpression(String expression){
		this.expression=expression;
	}
	
	/*------------------------------------
	 * implementation of expression pointcut
	 *----------------------------------*/
	@Override
	public ClassFilter getClassFilter() {
		checkReadyToMatch();
		return this;
	}

	
	@Override
	public MethodMatcher getMethodMatcher() {
		checkReadyToMatch();
		return this;
	}

	@Override
	public String getExpression() {
		return expression;
	}

	/*----------------------------------------------------------------------------
	 *implementation of introductionawaremethodmatcher
	 *---------------------------------------------------------------------------*/
	@Override
	public boolean matches(Class<?> targetClass, Method m, boolean hasIntroduction) {
		checkReadyToMatch();
		Method targetMethod= ClassUtils.getMostSpecificMethod(m, targetClass);
		ShadowMatch shadowMatch= getShadowMatch(m, targetMethod);
		
		if(shadowMatch.neverMatches()) return false;
		if(shadowMatch.alwaysMatches()) return true;
		
		//maybe match: then if has introduction return true;
		if(hasIntroduction) return true;
		
		return false;
	}
	
	@Override
	public boolean matches(Method method, Class<?> targetClass) {
		return matches(targetClass, method, false);
	}

	@Override
	public boolean matches(Method method, Class<?> targetClass, Object[] args) {
		checkReadyToMatch();
		
		Method targetMethod= ClassUtils.getMostSpecificMethod(method, targetClass);
		ShadowMatch shadowMatch= getShadowMatch(method, targetMethod);
		if(shadowMatch.alwaysMatches()) return true;
		if(shadowMatch.neverMatches()) return false;
		
		MethodInvocation mi= ExposedInvocationInterceptor.currentInvocation();
		Object targetObject= mi.getThis();
		
		if(mi instanceof ProxyMethodInvocation){
			throw new IllegalStateException("MethodInvocation is not a arc ProxyMethodInvocation:"+ mi);
		}
		
		ProxyMethodInvocation pmi=(ProxyMethodInvocation)mi;
		Object thisObject= pmi.getProxy();
		JoinPointMatch joinPointMatcher= shadowMatch.matchesJoinPoint(thisObject, targetObject, args);
		if(joinPointMatcher.matches()){
			bindParameters(pmi, joinPointMatcher);
		}
		
		return joinPointMatcher.matches();
	}
	
	private void bindParameters(ProxyMethodInvocation pmi, JoinPointMatch jpm){
		pmi.setUserAttribute(getExpression(), jpm);
	}
	
	private ShadowMatch getShadowMatch(Method originalMethod, Method targetMethod){
		ShadowMatch shadowMatch= shadowMatchCache.get(targetMethod);
		if(shadowMatch== null){
			synchronized(shadowMatchCache){
				shadowMatch= shadowMatchCache.get(targetMethod);
				if(shadowMatch== null){
					shadowMatch= pointcutExpression.matchesMethodExecution(targetMethod);
					
					if(shadowMatch== null){
						shadowMatch= pointcutExpression.matchesMethodExecution(originalMethod);
					}
					
					if(shadowMatch== null){
						shadowMatch= new ShadowMatchImpl(org.aspectj.util.FuzzyBoolean.NO, null, null, null);
					}
				}
				
				shadowMatchCache.put(targetMethod, shadowMatch);
			}
		}
		return shadowMatchCache.get(targetMethod);
	}

	@Override
	public boolean matches(Class<?> clz) {
		checkReadyToMatch();
		return pointcutExpression.couldMatchJoinPointsInType(clz);
	}
	
	/**
	 * check whether the match condition is ready
	 */
	private void checkReadyToMatch(){
		if(pointcutExpression== null){
			pointcutExpression= buildPointcutExpression();
		}
	}
	
	/**
	 * use the aspectj expression to build a pointcutexpression
	 * @return
	 */
	private PointcutExpression buildPointcutExpression(){
		PointcutParser parser= initializePointcutParser();
		PointcutParameter[] pointcutParams= new PointcutParameter[pointcutParameterNames.length];
		for(int i=0; i<pointcutParameterNames.length; i++){
			pointcutParams[i]= parser.createPointcutParameter(pointcutParameterNames[i], pointcutParameterTypes[i]);
		}
		return parser.parsePointcutExpression(getExpression(), pointcutDeclarationScope, pointcutParams);
	}
	
	/**
	 * initialize a pointcut parser
	 * @return
	 */
	private PointcutParser initializePointcutParser(){
		PointcutParser parser= PointcutParser.
				getPointcutParserSupportingSpecifiedPrimitivesAndUsingSpecifiedClassLoaderForResolution(
						supportedPointcutKinds, componentClassLoader);
		//register a extended primitive kind component
		parser.registerPointcutDesignatorHandler(new ComponentNamePointcutDesignatorHandler());
		return parser;
	}
	
	/**
	 * to handle a new add aspectj primitive type component
	 * @author sunzhongwei
	 *
	 */
	private class ComponentNamePointcutDesignatorHandler implements PointcutDesignatorHandler{

		private final static String COMPONENT_DESIGNATOR_NAME= "component";
		@Override
		public String getDesignatorName() {
			return COMPONENT_DESIGNATOR_NAME;
		}

		@Override
		public ContextBasedMatcher parse(String expression) {
			return new ComponentNameContextBaseMatcher(expression);
		}
		
	}
	
	/**
	 * to use to match the arc extend aspectj expression--component
	 * @author sunzhongwei
	 *
	 */
	private class ComponentNameContextBaseMatcher implements ContextBasedMatcher{
		private NamePattern expressionPattern;
		
		ComponentNameContextBaseMatcher(String expression){
			this.expressionPattern= new NamePattern(expression);
		}

		@Override
		public boolean couldMatchJoinPointsInType(Class arg0) {
			return false;
		}

		@Override
		public boolean couldMatchJoinPointsInType(Class arg0,
				MatchingContext arg1) {
			return false;
		}

		@Override
		public boolean matchesDynamically(MatchingContext arg0) {
			return true;
		}

		@Override
		/**
		 * match whether or not the component name is matched by the expression
		 */
		public FuzzyBoolean matchesStatically(MatchingContext arg0) {
			String advisedComponentName= ProxyCreationContext.getCurrentProxiedBeanName();
			return FuzzyBoolean.fromBoolean(expressionPattern.matches(advisedComponentName));
		}

		@Override
		public boolean mayNeedDynamicTest() {
			return false;
		}
		
	}

}
