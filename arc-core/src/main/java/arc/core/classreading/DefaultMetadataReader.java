package arc.core.classreading;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import org.objectweb.asm.ClassReader;
import arc.ioc.io.ResourceLoader;

public class DefaultMetadataReader implements MetadataReader {

	private AnnotationMetadata annotationMetadata;
	private ClassMetadata classMetadata;
	
	public DefaultMetadataReader(URL url, ResourceLoader resourceLoader) throws IOException{
		
		InputStream is= null;
		ClassReader cr;
		try{
			is= url.openStream();
			cr= new ClassReader(is);
		}finally{
			if(is!= null) is.close();
		}

		AnnotationMetadataReadingVisitor visitor= new AnnotationMetadataReadingVisitor(resourceLoader.getClassLoader());
		cr.accept(visitor, ClassReader.SKIP_FRAMES);
		classMetadata= visitor;
		annotationMetadata= visitor;
	}
	
	@Override
	public ClassMetadata getClassMetadata() {
		return classMetadata;
	}

	@Override
	public AnnotationMetadata getAnnotationMetada() {
		return annotationMetadata;
	}

}
