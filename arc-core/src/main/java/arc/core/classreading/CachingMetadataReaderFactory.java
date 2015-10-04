package arc.core.classreading;

import java.io.IOException;
import java.net.URL;
import java.text.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import arc.core.classreading.test.B;
import arc.core.classreading.test.B.C;
import arc.core.io.PathMatchingResourcePatternResolver;
import arc.core.io.ResourceLoader;

public class CachingMetadataReaderFactory implements MetadataReaderFactory {
	private ReadWriteLock rwl= new ReentrantReadWriteLock();
	
	private ResourceLoader resourceLoader;
	
	public CachingMetadataReaderFactory(ResourceLoader resourceLoader){
		this.resourceLoader= resourceLoader;
	}

	private Map<URL, MetadataReader> metadataReaderCache= new HashMap<URL, MetadataReader>();
	@Override
	public MetadataReader getMetadataReader(URL url) {
		rwl.readLock().lock();
		try{
			if(metadataReaderCache.containsKey(url)){
				return metadataReaderCache.get(url);
			}
		}finally{
			rwl.readLock().unlock();
		}
		
		try{
			rwl.writeLock().lock();
			MetadataReader mdr= new DefaultMetadataReader(url, resourceLoader);
			metadataReaderCache.put(url, mdr);
			rwl.readLock().lock();
		}catch (IOException e) {
			throw new IllegalStateException(e);
		}finally{
			rwl.writeLock().unlock();
		}
		
		try{
			return metadataReaderCache.get(url);
		}finally{
			rwl.readLock().unlock();
		}
	}	
	@Override
	public MetadataReader getMetadataReader(String className) {
		URL url= resourceLoader.getResource(className.replace(".", "/")+".class");
		return getMetadataReader(url);
	}
	
	public static void main(String[] args){
		MetadataReaderFactory readerFactory= new CachingMetadataReaderFactory(new PathMatchingResourcePatternResolver());
		MetadataReader reader= readerFactory.getMetadataReader(B.class.getName());
		AnnotationMetadata metadata= reader.getAnnotationMetada();
		Set<String> annotations= metadata.getAnnotations();
		
		for(String annotation: annotations){
			System.out.println(annotation);
		}
	}

}