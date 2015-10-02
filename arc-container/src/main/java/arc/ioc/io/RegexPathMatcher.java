package arc.ioc.io;

import java.util.regex.Pattern;

public class RegexPathMatcher implements PathMatcher {
	
	public boolean isPattern(String path){
		return path.indexOf('?')!=-1|| path.indexOf('*')!=-1;
	}

	@Override
	public boolean isMatch(String path, String regex) {
		return Pattern.matches(regex, path);
	}
}
