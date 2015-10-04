package arc.core.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.commons.lang.StringUtils;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class SchemaEntityResolver implements EntityResolver {
	static final String SCHEMA_MAPPING_FILE_PATH="META-INF/arc.schemas";
	@Override
	public InputSource resolveEntity(String publicId, String systemId)
			throws SAXException, IOException {
		
		if(!StringUtils.isBlank(systemId)){
			Properties properties=new Properties();
			properties.load(new FileInputStream(new File(SCHEMA_MAPPING_FILE_PATH)));
			String extendedSchemaPath=properties.getProperty(systemId);
			
			if(StringUtils.isNotBlank(extendedSchemaPath)){
				InputStream inputStream=getClass().getResourceAsStream(extendedSchemaPath);
				return new InputSource(inputStream);
			}
		}
		
		return null;
	}

}
