package arc.ioc.io;

public interface PathMatcher {
	
	boolean isMatch(String path, String regex);
	
	boolean isPattern(String path);
}
