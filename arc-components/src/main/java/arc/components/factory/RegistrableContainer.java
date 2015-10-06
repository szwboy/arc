package arc.components.factory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import arc.components.support.ComponentRegistry;
import arc.components.support.Scope;

public final class RegistrableContainer extends AbstractContainer implements ComponentRegistry{

	/**
	 * store all factories, every factory will generate a type of bean
	 */
	private ConcurrentHashMap<Key<?>, InternalFactory<?>> factories= new ConcurrentHashMap<Key<?>, InternalFactory<?>>();
	
	/**
	 * map component type and names
	 */
	private Map<Class<?>, Set<String>> factoriesByName;
	
	public <T>void factory(String name, final Class<T> type, final Class<? extends T> impl){
		
		InternalFactory<T> factory= new InternalFactory<T>(){

			@Override
			public T create(InternalContext context) {
				AbstractContainer container= (AbstractContainer) context.getContainer();
				ConstructorInjector<T> constructor= container.getConstructor(impl);
				return constructor.construct(context, type);
			}
			
		};
		
		factory(Key.newInstance(type, name), factory, Scope.Singleton);
	}
	
	@Override
	public <T> void constant(String name, String value, Class<T> type) {
		
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
			factoriesByName= new HashMap<Class<?>, Set<String>>();
			
			for(Key<?> key: factories.keySet()){
				if(!factoriesByName.containsKey(key.getType())){
					factoriesByName.put(key.getType(), new HashSet<String>());
				}

				factoriesByName.get(key.getType()).add(key.getName());
			}
		}
		
		return factoriesByName.get(type);
	}

	@Override
	public <T>boolean containesComponent(String name, Class<T> type) {
		return factories.containsKey(Key.newInstance(type, name));
	}

}
