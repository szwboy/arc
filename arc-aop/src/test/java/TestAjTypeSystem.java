import org.aspectj.lang.reflect.AjType;
import org.aspectj.lang.reflect.AjTypeSystem;


public class TestAjTypeSystem implements TestAjTypeSystemInterface{

	public static void main(String[] args){
		AjType ajType= AjTypeSystem.getAjType(TestAjTypeSystem.class);
		System.out.println(ajType.isAspect());
	}
}


