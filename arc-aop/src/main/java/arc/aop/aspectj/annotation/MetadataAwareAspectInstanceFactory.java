package arc.aop.aspectj.annotation;

import arc.aop.aspectj.AspectInstanceFactory;

public interface MetadataAwareAspectInstanceFactory extends AspectInstanceFactory {

	AspectMetadata getAspectMetadata();
}
