package arc.components.factory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import arc.components.support.ComponentRegistry;
import arc.components.support.Scope;
import arc.core.convert.Converter;
import arc.core.spi.ServiceLoader;

public final class RegistrableComponentFactory extends AbstractComponentFactory implements ComponentRegistry{

	/*store all factories, every factory will generate a type of bean*/
	private ConcurrentHashMap<Key<?>, InternalFactory<?>> factories= new ConcurrentHashMap<Key<?>, InternalFactory<?>>();
	/* map component type and names*/
	private Map<Class<?>, Set<String>> factoriesByName;
	
	/**
	 * create factory to generate bean. It will detect the constructor annoatated by {@link Inject} 
	 * and use that to create instance
	 */
	public <T>void factory(String name, final Class<T> impl, Scope scope){
		
		InternalFactory<T> factory= new InternalFactory<T>(){

			@Override
			public T create(InternalContext context) {
				AbstractComponentFactory componentFactory= (AbstractComponentFactory) context.getComponentFactory();
				ConstructorInjector<T> constructor= componentFactory.getConstructor(impl);
				return constructor.construct(context, impl);
			}
			
		};
		
		factory(Key.newInstance(impl, name), factory, scope);
	}
	
	@Override
	public <T> void constant(String name, final String value, Class<T> type, final Class<? extends T> impl) {
		InternalFactory<T> factory= new InternalFactory<T>(){

			@Override
			public T create(InternalContext context) {
				//use spi loader
				Converter converter= ServiceLoader.getLoader(Converter.class).getAdaptiveProvider();
				return converter.convert(value, impl);
			}
			
		};
		
		factory(Key.newInstance(type, name), factory, Scope.Singleton);
	}
	
	/**
	 * create specified factory by scope strategy
	 * @param key
	 * @param factory
	 * @param scope
	 */
	private <T>void factory(Key<T> key, InternalFactory<T> factory, Scope scope){
		InternalFactory<?> internalFactory= scope.scopeFactory(key, factory);
		factories.put(key, internalFactory);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected <T>InternalFactory<T> getFactory(Key<T> key) {
		return ((InternalFactory<T>) factories.get(key));
	}

	@Override
	public <T> Set<String> getComponentNames(Class<T> type) {
		
		if(factoriesByName== null){
			synchronized(this){
				if(factoriesByName== null)
					factoriesByName= new HashMap<Class<?>, Set<String>>();
			}
		}
		
		Set<String> factoryNamesByType= factoriesByName.get(type);
		if(factoryNamesByType== null){
		
			for(Key<?> key: factories.keySet()){
				if(!factoriesByName.containsKey(type)){
					synchronized(factoriesByName){
						if(!factoriesByName.containsKey(key)){
		
							Class<?> clz= key.getType();
							if(type.isAssignableFrom(clz)){
								factoriesByName.put(type, new HashSet<String>());
							}
						}
					}
				}
				
				factoriesByName.get(type).add(key.getName());
			}
		}
		
		return factoriesByName.get(type);
	}

	@Override
	public <T> boolean containesFactory(String name, Class<T> requiredType) {
		return factories.containsKey(Key.newInstance(requiredType, name));
	}

}
