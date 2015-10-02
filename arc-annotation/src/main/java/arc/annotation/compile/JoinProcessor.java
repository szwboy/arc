package arc.annotation.compile;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import arc.annotation.annotation.Spi;

@SupportedAnnotationTypes({"arc.common.annotation.Adaptive","arc.common.annotation.Spi"})
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class JoinProcessor extends AbstractProcessor {
	
	

	@Override
	public synchronized void init(ProcessingEnvironment env) {
		super.init(processingEnv);
		
		env.getFiler();
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations,
			RoundEnvironment env) {
		
		Set<? extends Element> elements= env.getElementsAnnotatedWith(Spi.class);
		for(Element ele: elements){
		}
		return false;
	}

}
