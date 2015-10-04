package arc.core.xml;

public class ParserContext {

	private ReaderContext readerContext;
	private ConfigParserDelegate parser;
	
	public ParserContext(ReaderContext readerContext,ConfigParserDelegate parser) {
		this.readerContext = readerContext;
		this.parser = parser;
	}

	public ReaderContext getReaderContext() {
		return readerContext;
	}

	public ConfigParserDelegate getParser() {
		return parser;
	}
	
}
