package arc.aop;

public interface ClassFilter {

	boolean matches(Class<?> clz);
	
	ClassFilter TRUE= TrueClassFilter.TRUE;
}
