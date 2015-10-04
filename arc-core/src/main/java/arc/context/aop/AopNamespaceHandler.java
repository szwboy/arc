package arc.context.aop;

import com.szw.xml.ConfigParser;
import com.szw.xml.NamespaceHandlerSupport;

public class AopNamespaceHandler extends NamespaceHandlerSupport {

	ConfigParser parser=new AopConfigParser();
	
	@Override
	public void init() {
		super.registerNamespaceParser("aspect", parser);
		super.registerNamespaceParser("pointcut", parser);
		super.registerNamespaceParser("introduction", parser);
	}

	
}
