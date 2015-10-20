package arc.aop.pointcut;

import arc.aop.Pointcut;

public interface ExpressionPointcut extends Pointcut {

	String getExpression();
}
