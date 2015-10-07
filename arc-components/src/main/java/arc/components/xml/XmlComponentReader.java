package arc.components.xml;

import java.io.InputStream;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import arc.components.support.ComponentRegistry;


public class XmlComponentReader implements ComponentReader {
	
	private Logger logger=Logger.getLogger(XmlComponentReader.class);

	private DocumentLoader documentLoader=new DefaultDocumentLoader();
	private NamespaceHandlerResolver resolver=new DefaultNamespaceHandlerResolver();
	
	private ComponentRegistry registry;
	
	//entity resolver to detect xml schema information such as where the schema information is 
	private EntityResolver entityResolver=new SchemaEntityResolver();
	private ErrorHandler errorHandler=new ErrorHandler(){

		@Override
		public void error(SAXParseException e) throws SAXException {
			throw e;
		}

		@Override
		public void fatalError(SAXParseException e) throws SAXException {
			throw e;
		}

		@Override
		public void warning(SAXParseException e) throws SAXException {
			//org.xml.sax.SAXParseException: SchemaLocation: schemaLocation value = '/com/szw/szw.xsd' must have even number of URI's.
			logger.warn(e.getCause());
		}
		
	};
	
	public XmlComponentReader(ComponentRegistry registry){
		this.registry= registry;
	}
	
	public void setRegistry(ComponentRegistry registry) {
		this.registry = registry;
	}
	
	@Override
	public void loadDefinition(String... pathes){
		for(String path:pathes){
			Document doc=loadDocument(path);
			doLoadDefinition(doc.getDocumentElement());
		}
	}

	public Logger getLogger() {
		return logger;
	}

	public DocumentLoader getDocumentLoader() {
		return documentLoader;
	}

	public NamespaceHandlerResolver getResolver() {
		return resolver;
	}

	public EntityResolver getEntityResolver() {
		return entityResolver;
	}

	public ErrorHandler getErrorHandler() {
		return errorHandler;
	}
	
	protected Document loadDocument(String path){
		InputStream inputStream=getClass().getResourceAsStream(path);
		InputSource inputSource=new InputSource(inputStream);
		return documentLoader.loadDocument(inputSource, entityResolver, errorHandler);
	}

	protected void doLoadDefinition(Element root){
		ComponentConfigParserDelegate parser=new ComponentConfigParserDelegate(createReaderContext());
		parser.parseConfig(root);
	}
	
	protected ReaderContext createReaderContext(){
		return new ReaderContext(resolver, this, registry);
	}
	
}

