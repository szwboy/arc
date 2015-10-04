package arc.ioc.xml;


public interface ConfigReader {

	void loadDefinition(String... path) throws Exception;
	
}
