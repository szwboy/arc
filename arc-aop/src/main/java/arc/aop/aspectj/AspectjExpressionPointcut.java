package arc.aop.aspectj;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.aspectj.weaver.tools.PointcutExpression;
import org.aspectj.weaver.tools.PointcutParameter;
import org.aspectj.weaver.tools.PointcutParser;
import org.aspectj.weaver.tools.PointcutPrimitive;

import arc.aop.ClassFilter;
import arc.aop.MethodMatcher;
import arc.aop.pointcut.ExpressionPointcut;

public class AspectjExpressionPointcut implements ExpressionPointcut, ClassFilter, MethodMatcher {
	private ClassLoader componentClassLoader;
	private Set<PointcutPrimitive> supportedPointcutKinds= new HashSet<PointcutPrimitive>();
	
	private Class<?> pointcutDeclarationScope;
	private Class<?>[] pointcutParameterTypes= new Class<?>[0];
	private String[] pointcutParameterNames= new String[0];
	
	private void initPointcutPrimitive(){
		supportedPointcutKinds.add(PointcutPrimitive.EXECUTION);
		supportedPointcutKinds.add(PointcutPrimitive.ARGS);
		supportedPointcutKinds.add(PointcutPrimitive.REFERENCE);
		supportedPointcutKinds.add(PointcutPrimitive.THIS);
		supportedPointcutKinds.add(PointcutPrimitive.WITHIN);
		supportedPointcutKinds.add(PointcutPrimitive.TARGET);
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
	
	private PointcutParser initializePointcutParser(){
		PointcutParser parser= PointcutParser.
				getPointcutParserSupportingSpecifiedPrimitivesAndUsingSpecifiedClassLoaderForResolution(
						supportedPointcutKinds, componentClassLoader);
		
		return parser;
	}
	
	@Override
	public ClassFilter getClassFilter() {
		return null;
	}

	@Override
	public MethodMatcher getMethodMatcher() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getExpression() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean matches(Method method, Class<?> clz) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean matches(Method method, Class<?> clz, Object[] args) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean matches(Class<?> clz) {
		// TODO Auto-generated method stub
		return false;
	}

}
