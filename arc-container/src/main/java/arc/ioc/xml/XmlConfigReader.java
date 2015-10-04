package arc.ioc.xml;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


public class XmlConfigReader implements ConfigReader {
	
	private Logger logger=Logger.getLogger(XmlConfigReader.class);
	
	private DocumentLoader documentLoader=new DefaultDocumentLoader();
	private NamespaceHandlerResolver resolver=new DefaultNamespaceHandlerResolver();
	
	private ConfigParserDelegate parser;
	
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
	
	public XmlConfigReader(String... pathes) throws Exception{
		loadDefinition(pathes);
	}
	
	@Override
	public void loadDefinition(String... pathes) throws Exception {
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

	public ConfigParserDelegate getParser() {
		return parser;
	}

	public EntityResolver getEntityResolver() {
		return entityResolver;
	}

	public ErrorHandler getErrorHandler() {
		return errorHandler;
	}
	
	protected Document loadDocument(String path) throws Exception{
		InputStream inputStream=getClass().getResourceAsStream(path);
		InputSource inputSource=new InputSource(inputStream);
		return documentLoader.loadDocument(inputSource, entityResolver, errorHandler);
	}

	protected void doLoadDefinition(Element root){
		parser=new ConfigParserDelegate(createReaderContext());
		try {
			parser.parseConfig(root);
		} catch (Exception e) {
		}
	}
	
	protected ReaderContext createReaderContext(){
		return null;
				//new ReaderContext(resolver,this,this);
	}
	
}

