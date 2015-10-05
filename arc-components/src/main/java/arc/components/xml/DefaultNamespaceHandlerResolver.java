package arc.components.xml;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;

public class DefaultNamespaceHandlerResolver implements NamespaceHandlerResolver {
	private static final String DEFAULT_HANLDER_MAPPING_PATH="META-INF/arc.handlers";
	private volatile Properties handlerMapping;
	private volatile Map<String,NamespaceHandler> handlers;
	
	@Override
	public NamespaceHandler resolve(String nameSpaceUri) {
		if(null==handlers) handlers=new HashMap<String,NamespaceHandler>();
		
		NamespaceHandler handler=handlers.get(nameSpaceUri);
		if(handler==null){
			String handlerClassName=getNameSpaceHandler(nameSpaceUri);
			
			if(StringUtils.isBlank(handlerClassName)) return null;

			try {
				handler= (NamespaceHandler) ClassUtils.getClass(handlerClassName).newInstance();
			} catch (InstantiationException e) {
				throw new IllegalArgumentException("cannot find corresponding handler["+nameSpaceUri+"]");
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			handler.init();
			handlers.put(nameSpaceUri, handler);
		}
		
		return handler;
		
	}
	
	private String getNameSpaceHandler(String nameSpaceUri){
		if(handlerMapping==null){
			try {
				handlerMapping=new Properties();
				URL realPath=Thread.currentThread().getContextClassLoader().getResource(DEFAULT_HANLDER_MAPPING_PATH);
				handlerMapping.load(new BufferedReader(new InputStreamReader(realPath.openStream())));
			} catch (FileNotFoundException e) {
			} catch (IOException e) {
			}
		}
		
		return handlerMapping.getProperty(nameSpaceUri);
		
	}

}
