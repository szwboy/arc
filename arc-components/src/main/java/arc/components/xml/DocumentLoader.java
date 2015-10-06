package arc.components.xml;

import javax.xml.XMLConstants;

import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;

public interface DocumentLoader {
	String SCHEMA_LANGUAGE_ATTR="http://java.sun.com/xml/jaxp/properties/schemaLanguage";
	String XSD_SCHEMA_LANGUAGE=XMLConstants.W3C_XML_SCHEMA_NS_URI;

	public Document loadDocument(InputSource inputSource,EntityResolver resolver, ErrorHandler handler);
}
