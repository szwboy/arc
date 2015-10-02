package arc.ioc.container.classreading;

/**
 * Read class metadata
 * @author sunzhongwei
 *
 */
public interface MetadataReader {

	ClassMetadata getClassMetadata();
	
	AnnotationMetadata getAnnotationMetada();
}
