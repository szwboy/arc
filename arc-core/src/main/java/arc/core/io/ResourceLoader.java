package arc.core.io;

import java.net.URL;


public interface ResourceLoader {
	ClassLoader getClassLoader();
	
	URL getResource(String location);
}
