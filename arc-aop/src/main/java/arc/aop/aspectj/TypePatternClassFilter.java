package arc.aop.aspectj;

import org.aspectj.weaver.tools.PointcutParser;
import org.aspectj.weaver.tools.TypePatternMatcher;

import arc.aop.ClassFilter;

public class TypePatternClassFilter implements ClassFilter {

	private String typePattern;
	
	private TypePatternMatcher aspectjTypePatternMatcher;
	
	public TypePatternClassFilter(String typePattern){
		setTypePattern(typePattern);
	}
	
	public void setTypePattern(String typePattern){
		this.typePattern= typePattern;
		this.aspectjTypePatternMatcher= PointcutParser.
				getPointcutParserSupportingAllPrimitivesAndUsingContextClassloaderForResolution().
				parseTypePattern(typePattern);
	}
	
	@Override
	public boolean matches(Class<?> clz) {
		if(aspectjTypePatternMatcher== null){
			throw new IllegalArgumentException("no 'typePattern' has been set");
		}
		return aspectjTypePatternMatcher.matches(clz);
	}

}
