package arc.aop.aspectj.annotation;

/**
 * aspect isntance factory decorator to ensure instantiate once
 * @author sunzhongwei
 *
 */
public class LazySingletonAspectInstanceFactoryDecorator implements MetadataAwareAspectInstanceFactory {

	private MetadataAwareAspectInstanceFactory maaif;
	Object singleton;
	
	public LazySingletonAspectInstanceFactoryDecorator(MetadataAwareAspectInstanceFactory maaif){
		this.maaif= maaif;
	}
	
	@Override
	public Object getAspectInstance() {
		if(maaif== null){
			synchronized(this){
				if(maaif== null){
					
					singleton= maaif.getAspectInstance();
				}
			}
		}
		return singleton;
	}

	@Override
	public ClassLoader getAspectClassLoader() {
		return maaif.getAspectClassLoader();
	}

	@Override
	public AspectMetadata getAspectMetadata() {
		return maaif.getAspectMetadata();
	}

}
