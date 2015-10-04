package arc.core.classreading;

import java.util.Map;

public interface MemberMetadata {

	boolean isStatic();
	
	boolean isFinal();
	
	boolean isAnnotated(String annotationType);
	
	Map<String, Object> getAnnotationAttributes(String annotationType);
}
