package arc.aop.aspectj.annotation;

import arc.components.factory.RegistrableComponentFactory;

public class AspectInstanceFactoy implements MetadataAwareAspectInstanceFactory {

	private AspectMetadata aspectMetadata;
	
	private ClassLoader classLoader;
	
	private RegistrableComponentFactory componentFactory;
	
	public AspectInstanceFactoy(String aspectName, RegistrableComponentFactory componentFactory){
		this(componentFactory.getType(aspectName), aspectName, componentFactory);
	}
	
	public AspectInstanceFactoy(Class<?> type, String aspectName, RegistrableComponentFactory componentFactory){
		this.aspectMetadata= new AspectMetadata(type, aspectName);
		this.componentFactory= componentFactory;
	}
	
	@Override
	public Object getAspectInstance() {
		String aspectName= aspectMetadata.getAspectName();
		return componentFactory.getComponent(aspectName);
	}

	@Override
	public ClassLoader getAspectClassLoader() {
		return classLoader;
	}

	@Override
	public AspectMetadata getAspectMetadata() {
		return aspectMetadata;
	}

}
