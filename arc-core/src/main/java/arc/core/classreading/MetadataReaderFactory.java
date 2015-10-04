package arc.core.classreading;

import java.net.URL;

/**
 * create a reader to read meta data
 * @author sunzhongwei
 *
 */
public interface MetadataReaderFactory {

	MetadataReader getMetadataReader(URL url);
	
	MetadataReader getMetadataReader(String className);
}
