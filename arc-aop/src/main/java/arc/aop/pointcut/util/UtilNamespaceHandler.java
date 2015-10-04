package arc.context.util;

import arc.context.ConfigParser;
import arc.context.NamespaceHandlerSupport;


public class UtilNamespaceHandler extends NamespaceHandlerSupport{

	ConfigParser utilParser=new UtilConfigParser();
	
	@Override
	public void init() {
		super.registerNamespaceParser("map", utilParser);
		super.registerNamespaceParser("collection", utilParser);
		super.registerNamespaceParser("array", utilParser);
	}

}
