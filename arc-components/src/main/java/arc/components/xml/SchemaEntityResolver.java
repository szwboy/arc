package arc.components.xml;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class SchemaEntityResolver implements EntityResolver {
	static final String SCHEMA_MAPPING_FILE_PATH="META-INF/arc.schemas";
	private Properties schemaCache;
	@Override
	public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {

		ClassLoader cl= Thread.currentThread().getContextClassLoader();
		if(schemaCache== null){
			synchronized(this){
				
				if(schemaCache== null){
					schemaCache= new Properties();
					Enumeration<URL> urls= cl.getResources(SCHEMA_MAPPING_FILE_PATH);
					while(urls!=null&& urls.hasMoreElements()){
						schemaCache.load(urls.nextElement().openStream());
					}
					
				}
			}
		}
		
		if(!StringUtils.isBlank(systemId)){
			
			String extendedSchemaPath=schemaCache.getProperty(systemId);
			if(StringUtils.isNotBlank(extendedSchemaPath)){
				InputStream inputStream=cl.getResourceAsStream(extendedSchemaPath);
				return new InputSource(inputStream);
			}
		}
		
		return null;
	}

}
