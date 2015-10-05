package arc.components.xml;

public class ParserContext {

	private ReaderContext readerContext;
	private ComponentConfigParserDelegate parser;
	
	public ParserContext(ReaderContext readerContext,ComponentConfigParserDelegate parser) {
		this.readerContext = readerContext;
		this.parser = parser;
	}

	public ReaderContext getReaderContext() {
		return readerContext;
	}

	public ComponentConfigParserDelegate getParser() {
		return parser;
	}
	
}
