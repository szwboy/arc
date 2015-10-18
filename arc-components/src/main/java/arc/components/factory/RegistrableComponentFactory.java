package arc.components.factory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import arc.components.support.ComponentRegistry;
import arc.components.support.Scope;
import arc.core.convert.Converter;
import arc.core.spi.ServiceLoader;

public final class RegistrableComponentFactory extends AbstractComponentFactory implements ComponentRegistry{

	/*store all factories, every factory will generate a type of bean*/
	private ConcurrentHashMap<String, InternalFactory<?>> factories= new ConcurrentHashMap<String, InternalFactory<?>>();
	/* map component type and names*/
	private Map<Class<?>, Set<String>> factoriesByName;
	
	/*====================================================================
	 *implementation of ComponentRegistry
	 *====================================================================*/
	public <T>void factory(String name, final Class<T> impl, Scope scope){
		
		InternalFactory<T> factory= new InternalFactory<T>(){

			@Override
			public T create(InternalContext context) {
				AbstractComponentFactory componentFactory= (AbstractComponentFactory) context.getComponentFactory();
				ConstructorInjector<T> constructor= componentFactory.getConstructor(impl);
				
				//post processor bean
				T t= constructor.construct(context, impl);
				Set<String> names= getComponentNames(ComponentPostProcessor.class);
				
				return constructor.construct(context, impl);
			}

			@Override
			public Class<T> getType() {
				return impl;
			}
			
		};
		
		factory(name, factory, scope);
	}
	
	public <T>void factory(String name, final Class<T> impl, final T t, Scope scope){
		InternalFactory<T> factory= new InternalFactory<T>(){

			@Override
			public T create(InternalContext context) {
				return t;
			}

			@Override
			public Class<T> getType() {
				return impl;
			}
			
		};
		
		factory(name, factory, scope);
	}
	
	@Override
	public <T> void constant(String name, final String value, Class<? super T> type, final Class<T> impl) {
		InternalFactory<T> factory= new InternalFactory<T>(){

			@Override
			public T create(InternalContext context) {
				//use spi loader
				Converter converter= ServiceLoader.getLoader(Converter.class).getAdaptiveProvider();
				return converter.convert(value, impl);
			}

			@Override
			public Class<T> getType() {
				return impl;
			}
			
		};
		
		factory(name, factory, Scope.Singleton);
	}
	
	/**
	 * create specified factory by scope strategy
	 * @param key
	 * @param factory
	 * @param scope
	 */
	private <T>void factory(String name, InternalFactory<T> factory, Scope scope){
		InternalFactory<?> internalFactory= scope.scopeFactory(name, factory);
		factories.put(name, internalFactory);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected <T>InternalFactory<T> getFactory(String name) {
		return ((InternalFactory<T>) factories.get(name));
	}

	@Override
	public Set<String> getComponentNames(Class<?> type) {
		
		if(factoriesByName== null){
			synchronized(this){
				if(factoriesByName== null)
					factoriesByName= new HashMap<Class<?>, Set<String>>();
			}
		}
		
		Set<String> factoryNamesByType= factoriesByName.get(type);
		if(factoryNamesByType== null){
		
			for(Entry<String, InternalFactory<?>> entry: factories.entrySet()){
				Class<?> clz= entry.getValue().getType();
				if(type.isAssignableFrom(clz)){
					synchronized(factoriesByName){
						if(!factoriesByName.containsKey(type)){
							if(!factoriesByName.containsKey(entry.getKey())){
								factoriesByName.put(type, new HashSet<String>());
							}
						}
						factoriesByName.get(type).add(entry.getKey());
					}	
				}
				
			}
		}
		
		return factoriesByName.get(type);
	}

	@Override
	public <T> boolean containesFactory(String name) {
		return factories.containsKey(name);
	}

}
