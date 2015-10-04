package arc.core.xml;


public interface IConfigReader {
	
	String docType="DOCTYPE";
	String startComment="<!--";
	String endComment="-->";
	int XSDMODE=1;
	int DTDMODE=2;
	int AUTOMODE=0;

	void loadDefinition(String... path) throws Exception;
	
}
