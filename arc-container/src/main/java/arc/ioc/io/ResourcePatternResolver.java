package arc.ioc.io;

import java.net.URL;

public interface ResourcePatternResolver extends ResourceLoader {

	URL[] getResources(String baseLocation);
}
