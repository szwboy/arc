package arc.ioc.container.classreading;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;

import arc.ioc.io.PathMatchingResourcePatternResolver;
import arc.ioc.io.ResourcePatternResolver;

public class ClasspathAnnotationScanner {
	private String[] basePackages;
	
	private ResourcePatternResolver resolver= new PathMatchingResourcePatternResolver();
	private ClassVisitor visitor= new AnnotationMetadataReadingVisitor(resolver.getClassLoader());
	private Map<URL, AnnotationMetadata> annotationMetadatas= new ConcurrentHashMap<URL, AnnotationMetadata>();
	
	public ClasspathAnnotationScanner(String basePackage){
		basePackages= basePackage.split(",");	
	}
	
	public void scan() throws IOException{
		for(int i=0;basePackages!=null&& i<basePackages.length;i++){
			String basePackage= basePackages[i];
			URL[] urls= resolver.getResources(basePackage);
			
			if(urls!= null){
				
				for(URL url: urls){
					InputStream is= url.openStream();
					try {
						
						ClassReader reader = new ClassReader(is);
						reader.accept(visitor, ClassReader.SKIP_DEBUG);
						
						annotationMetadatas.put(url, (AnnotationMetadataReadingVisitor)visitor);
					}finally{
						is.close();
					}
				}
			}
		}
	}
	
}
