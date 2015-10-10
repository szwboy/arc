package arc.container.config;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import arc.components.support.ComponentRegistry;
import arc.components.xml.Component;
import arc.components.xml.ComponentConfigParser;
import arc.components.xml.ParserContext;
import arc.container.annotation.ClasspathAnnotationScanner;
import arc.core.classreading.AnnotationMetadataReadingVisitor;
import arc.core.classreading.CachingMetadataReaderFactory;
import arc.core.classreading.MetadataReaderFactory;

public class ScanComponentConfigParser implements ComponentConfigParser {

	public static final String BASE_PACKAGE_ATTRIBUTE="base-package";
	public static final String RESOURCE_PATTERN_ATTRIBUTE="resource-pattern";
	
	public ScanComponentConfigParser(){
	}
	
	@Override
	public void parse(Element e, ParserContext parserContext) {
		String basePackage= e.getAttribute(BASE_PACKAGE_ATTRIBUTE);
		String resourcePattern= e.getAttribute(RESOURCE_PATTERN_ATTRIBUTE);
		
		if(StringUtils.isBlank(basePackage)){
			throw new IllegalStateException("base package attribute cannot null");
		}
		
		ClasspathAnnotationScanner scanner= new ClasspathAnnotationScanner(parserContext.getReaderContext().getRegistry());
		try {
			scanner.scan(basePackage);
		} catch (IOException e1) {
			throw new IllegalStateException(e1);
		}
	}

}
