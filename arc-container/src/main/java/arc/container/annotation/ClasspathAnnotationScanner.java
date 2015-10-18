package arc.container.annotation;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;

import arc.components.support.ComponentRegistry;
import arc.components.support.Scope;
import arc.core.classreading.AnnotationMetadata;
import arc.core.classreading.CachingMetadataReaderFactory;
import arc.core.classreading.MetadataReader;
import arc.core.classreading.MetadataReaderFactory;
import arc.core.io.PathMatchingResourcePatternResolver;
import arc.core.io.ResourcePatternResolver;

public class ClasspathAnnotationScanner {
	
	private ResourcePatternResolver resolver= new PathMatchingResourcePatternResolver();
	private MetadataReaderFactory metadataReaderFactory= new CachingMetadataReaderFactory(resolver);
	private ComponentRegistry registry;
	
	public ClasspathAnnotationScanner(ComponentRegistry registry){
		this.registry= registry;
			
	}
	
	public void scan(String basePackage) throws IOException{
		String[] basePackages= basePackage.split(",");
		for(int i=0;basePackages!=null&& i<basePackages.length;i++){
			String pack= basePackages[i];
			URL[] urls= resolver.getResources(pack);
			
			if(urls!= null){
				
				for(URL url: urls){
					MetadataReader metadataReader= metadataReaderFactory.getMetadataReader(url);
					try {
						
						final AnnotationMetadata anm= metadataReader.getAnnotationMetada();
						Set<String> annotations= anm.getAnnotations();
						
						for(final String ann: annotations){
							if(isStereotype(anm, ann)){
								
								registry.factory(determineBeanName(anm, ann), ClassUtils.getClass(anm.getName()), Scope.Singleton);
								break;
							}
						}
						
					} catch (ClassNotFoundException e) {
						throw new RuntimeException(e);
					}
				}
				
				
			}
		}
	}
	
	private String determineBeanName(AnnotationMetadata anm, String ann){
		Map<String, Object> annotationAttributes= anm.getAnnotationAttributes(ann);
		Object beanName= annotationAttributes.get("value");
		if((beanName instanceof String)&& StringUtils.isNotBlank((String)beanName)){
			return (String)beanName;
		}
		
		String type= anm.getName();
		if(anm.getEnclosingClass()!= null){
			
		}else{
			String strToTrans= type.substring(type.lastIndexOf('.')+1);
			beanName= Character.toLowerCase(strToTrans.charAt(0))+strToTrans.substring(1);
		}
		
		return (String)beanName;
	}
	
	private boolean isStereotype(AnnotationMetadata anm, String ann){
		Set<String> metaAnnotations= anm.getMetaAnnotation(ann);
		
		if(ann.equals("arc.core.stereotype.Component")||
				metaAnnotations.contains("arc.core.stereotype.Component")){
			return true;
		}
		
		return false;
	}
	
}
