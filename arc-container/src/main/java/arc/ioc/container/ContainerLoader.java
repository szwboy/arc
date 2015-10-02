package arc.ioc.container;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import arc.ioc.container.Container.ConstructorInjector;
import arc.ioc.container.Container.ContextualCallable;

public class ContainerLoader {
	@SuppressWarnings("rawtypes")
	private ConcurrentHashMap factories= new ConcurrentHashMap();
	@SuppressWarnings("rawtypes")
	private Set<InternalFactory> singletonFactories= new HashSet<InternalFactory>();
	
	private boolean created;

	public <T>void factory(String name, final Class<T> type, final Class<? extends T> impl){
		
		InternalFactory<T> factory= new InternalFactory<T>(){

			@Override
			public T create(InternalContext context) {
				Container container= context.getContainer();
				ConstructorInjector<T> constructor= container.getConstructor(impl);
				return constructor.construct(context, type);
			}
			
		};
		
		factory(Key.newInstance(type, name), factory, Scope.Singleton);
	}
	
	@SuppressWarnings("unchecked")
	private <T>void factory(Key<T> key, InternalFactory<T> factory, Scope scope){
		factories.put(key, factory);
		if(scope== Scope.Singleton){
			singletonFactories.add(factory);
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	Container create(boolean loadSingleton){
		if(created) throw new IllegalStateException("container is in creation");
		created= true;
		Container container= new Container(factories);
		if(loadSingleton){
			for(final InternalFactory factory: singletonFactories){

				container.callInContext(new ContextualCallable(){

					public Object call(InternalContext context) {
						return factory.create(context);
					}
					
				});
			}
		}
		
		return container;
	}
}
