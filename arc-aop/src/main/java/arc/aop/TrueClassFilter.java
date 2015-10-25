package arc.aop;

public class TrueClassFilter implements ClassFilter {
	public static final ClassFilter TRUE= new TrueClassFilter();
	
	private TrueClassFilter(){}

	@Override
	public boolean matches(Class<?> clz) {
		return true;
	}

}
