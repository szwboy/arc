package arc.core.xml;

import java.io.IOException;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class EntityResolverWrapper implements EntityResolver{

	private EntityResolver resolver;
	
	public EntityResolverWrapper(EntityResolver resolver){
		this.resolver=resolver;
	}
	
	@Override
	public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
		return resolver.resolveEntity(publicId, systemId);
	}
	
	public void setResolver(EntityResolver resolver) {
		this.resolver = resolver;
	}
	
}
