package arc.components.xml;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;

public class DefaultDocumentLoader implements DocumentLoader {

	@Override
	public Document loadDocument(InputSource inputSource,
			EntityResolver resolver, ErrorHandler handler){
		DocumentBuilderFactory builderFactory=createBuilderFactory();
		Document doc;
		try {
			DocumentBuilder builder = createDocumentBuilder(builderFactory, resolver, handler);
			doc= builder.parse(inputSource);
		} catch (ParserConfigurationException e) {
			throw new IllegalStateException("create documentbuilder failure", e);
		} catch (Throwable t) {
			throw new IllegalStateException("parse config xml error", t);
		}
		
		return doc;
	}
	
	private DocumentBuilderFactory createBuilderFactory(){
		DocumentBuilderFactory builderFactory=DocumentBuilderFactory.newInstance();
		builderFactory.setValidating(true);
		builderFactory.setNamespaceAware(true);
		builderFactory.setAttribute(SCHEMA_LANGUAGE_ATTR,XSD_SCHEMA_LANGUAGE);
		
		return builderFactory;
	}
	
	private DocumentBuilder createDocumentBuilder(DocumentBuilderFactory builderFactory,
			EntityResolver resolver, ErrorHandler handler) throws ParserConfigurationException{
		DocumentBuilder builder = builderFactory.newDocumentBuilder();
		if(resolver!=null)
			builder.setEntityResolver(resolver);
		
		if(handler!=null)
			builder.setErrorHandler(handler);
		
		return builder;
	}

}
