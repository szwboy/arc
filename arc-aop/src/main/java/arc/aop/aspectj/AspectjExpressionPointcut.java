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
import arc.core.util.ClassUtils;

public class AspectjExpressionPointcut implements ExpressionPointcut, ClassFilter, MethodMatcher {
	private ClassLoader componentClassLoader;
	private Set<PointcutPrimitive> supportedPointcutKinds= new HashSet<PointcutPrimitive>();
	
	private Class<?> pointcutDeclarationScope;
	private Class<?>[] pointcutParameterTypes= new Class<?>[0];
	private String[] pointcutParameterNames= new String[0];
	
	private PointcutExpression pointcutExpression;
	private String expression;
	private Map<Method, ShadowMatch> shadowMatchCache= new HashMap<Method, ShadowMatch>();
	
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
	
	public AspectjExpressionPointcut(Class<?> pointcutDeclarationScope, String[] paramNames, Class<?>[] paramTypes){
		initPointcutPrimitive();
		this.pointcutParameterTypes= paramTypes;
		this.pointcutParameterNames= paramNames;
	}

	private PointcutExpression buildPointcutExpression(){
		PointcutParser parser= initializePointcutParser();
		PointcutParameter[] pointcutParams= new PointcutParameter[pointcutParameterNames.length];
		for(int i=0; i<pointcutParameterNames.length; i++){
			pointcutParams[i]= parser.createPointcutParameter(pointcutParameterNames[i], pointcutParameterTypes[i]);
		}
		return parser.parsePointcutExpression(getExpression(), pointcutDeclarationScope, pointcutParams);
	}
	
	public void setExpression(String expression){
		this.expression=expression;
	}
	
	/**
	 * initialize a pointcut parser and register the new added primitive type component
	 * @return
	 */
	private PointcutParser initializePointcutParser(){
		PointcutParser parser= PointcutParser.
				getPointcutParserSupportingSpecifiedPrimitivesAndUsingSpecifiedClassLoaderForResolution(
						supportedPointcutKinds, componentClassLoader);
		parser.registerPointcutDesignatorHandler(new ComponentNamePointcutDesignatorHandler());
		return parser;
	}
	
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

	@Override
	public boolean matches(Method method, Class<?> targetClass) {
		checkReadyToMatch();
		
		Method targetMethod= ClassUtils.getMostSpecificMethod(method, targetClass);
		ShadowMatch shadowMatch= getShadowMatch(method, targetMethod);
		if(shadowMatch.alwaysMatches()) return true;
		if(shadowMatch.neverMatches()) return false;
		
		return false;
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
	
	private void checkReadyToMatch(){
		if(pointcutExpression== null){
			pointcutExpression= buildPointcutExpression();
		}
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
		public ContextBasedMatcher parse(String arg0) {
			return new ComponentNameContextBaseMatcher(arg0);
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
