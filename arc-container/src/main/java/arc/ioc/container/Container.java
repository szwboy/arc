package arc.ioc.container;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ReflectPermission;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javassist.NotFoundException;

import org.apache.commons.lang.StringUtils;

import arc.annotation.annotation.Inject;
import arc.annotation.annotation.Qualifier;
import arc.annotation.annotation.Value;
import arc.common.utils.ReflectUtils;
import arc.ioc.cache.ReferenceCache;

public class Container {
	
	@SuppressWarnings("rawtypes")
	private Map<Key, InternalFactory> factories;
	@SuppressWarnings("rawtypes")
	private Map<Class, Set<String>> factoriesByName;
	
	private ThreadLocal<InternalContext[]> localContext= new ThreadLocal<InternalContext[]>(){
		InternalContext[] initialVlaue(){
			return new InternalContext[1];
		}
	};
	
	@SuppressWarnings("rawtypes")
	public Container(Map<Key, InternalFactory> factories){
		this.factories= factories;
		
		Map<Class, Set<String>> map= new HashMap<Class, Set<String>>();
		for(Entry<Key, InternalFactory> en: factories.entrySet()){
			
			Key key= en.getKey();
			if(!map.containsKey(key.getType())){
				Set<String> set= new HashSet<String>();
				map.put(key.getType(), set);
			}
			
			map.get(key.getType()).add(key.getName());
		}
		
		factoriesByName= Collections.unmodifiableMap(map);
	}
	
	@SuppressWarnings("rawtypes")
	private ReferenceCache<Class,ConstructorInjector> constructors= new ReferenceCache<Class,ConstructorInjector>(){

		@SuppressWarnings("unchecked")
		@Override
		protected ConstructorInjector doCreate(Class key) {
			
			return new ConstructorInjector(key);
		}
		
	};
	
	private void addInjectorsForMethod(List<Injector> injectors, Class<?> type){
		Method[] ms= type.getDeclaredMethods();
		for(Method m: ms){
			if(m.isAnnotationPresent(Inject.class))
				injectors.add(new MethodInjector(m));
		}
	}
	
	private void addInjectorsForField(List<Injector> injectors, Class<?> type){
		Field[] fs= type.getDeclaredFields();
		for(Field f: fs){
			if(f.isAnnotationPresent(Value.class)|| f.isAnnotationPresent(Qualifier.class))
				injectors.add(new FieldInjector(f, this));
		}
	}
	
	private void addInjectorsForMember(Class<?> type, List<Injector> injectors){
		if(type== Object.class) return;
		addInjectorsForMember(type.getSuperclass(), injectors);
		
		addInjectorsForField(injectors, type);
		addInjectorsForMethod(injectors, type);
	}
	
	@SuppressWarnings("unchecked")
	<T>ConstructorInjector<T> getConstructor(Class<? extends T> impl){
		return constructors.get(impl);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	ParameterInjector<?>[] createParameter(Annotation[][] annss, Class<?>[] pts, Class<?> declaringClass){
		ParameterInjector<?>[] parameterInjectors= new ParameterInjector<?>[pts.length];
		
		for(int i=0;i<annss.length;i++){
			Annotation[] anns= annss[i];
			
			String name="";
			String value="";
			for(Annotation ann: anns){
				if(ann instanceof Qualifier){
					name= ((Qualifier) ann).value();
				}
				
				if(ann instanceof Value){
					value= ((Value) ann).value();
				}
				
				if(StringUtils.isNotBlank(value)&&StringUtils.isNotBlank(name)){
					throw new RuntimeException("either value or name has value");
				}
				
				if(StringUtils.isNotBlank(value)){
					// to do
					
				}
				
				if(StringUtils.isBlank(name)){
					try {
						name= ReflectUtils.detectConstructorPn(declaringClass, pts, i);
					} catch (NotFoundException e) {
						throw new RuntimeException("cannot auto detect parameter name for the parameter:"+i);
					}
				}
				
				Key<?> key= Key.newInstance(pts[i], name);
				ExternalContext<?> context= new ExternalContext(this, key);
				parameterInjectors[i]= new ParameterInjector(context, factories.get(key));
			}
			
		}
		
		return parameterInjectors;
	}
	
	Object[] getParameters(ParameterInjector<?>[] parameterInjectors, InternalContext context){
		Object[] parameters= new Object[parameterInjectors.length];
		for(int i=0;i< parameterInjectors.length;i++){
			parameters[i]= parameterInjectors[i].inject(context);
		}
		
		return parameters;
	}
	
	class ConstructorInjector<T>{
		ParameterInjector<?>[] parameterInjectors;
		Constructor<? extends T> constructor;
		List<Injector> injectors;
		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public ConstructorInjector(Class<? extends T> impl){
			Constructor<T>[] constructors= (Constructor<T>[]) impl.getDeclaredConstructors();
			
			for(Constructor<T> cons: constructors){
				if(cons.isAnnotationPresent(Inject.class)){
					this.constructor= cons;
					if(!constructor.isAccessible()){
						SecurityManager sm= System.getSecurityManager();
						sm.checkPermission(new ReflectPermission("supressAccessCheck"));
						constructor.setAccessible(true);
					}
					
					Class[] pts= cons.getParameterTypes();
					Annotation[][] annss= cons.getParameterAnnotations();
					parameterInjectors= createParameter(annss, pts, impl);
					
				}
			}
			
			addInjectorsForMember(impl, injectors= new ArrayList<Injector>());
		}
		
		T construct(InternalContext context, Class<T> type){
			Object[] parameters= getParameters(parameterInjectors, context);
			try {
				T t= constructor.newInstance(parameters);
				
				for(Injector injector: injectors){
					injector.inject(context, t);
				}
				return t;
			} catch (Throwable e) {
				Throwable t= e.getCause();
				if(t instanceof RuntimeException) throw (RuntimeException)t;
				if(t instanceof Error) throw (Error)t;
				
				throw new RuntimeException(e);
			} 
		}
	}
	
	class ParameterInjector<T>{
		ExternalContext<T> context;
		InternalFactory<T> factory;
		
		ParameterInjector(ExternalContext<T> context, InternalFactory<T> factory){
			this.context= context;
			this.factory= factory;
		}
		
		public T inject(InternalContext context) {
			ExternalContext<?> previous= context.getExternalContext();
			try{
				context.setExternalContext(this.context);
				T t= factory.create(context);
				return t;
			}finally{
				context.setExternalContext(previous);
			}
			
		}
		
	}
	
	class FieldInjector implements Injector{
		Field f;
		ExternalContext<?> externalContext;
		InternalFactory<?> factory;
		@SuppressWarnings({ "unchecked", "rawtypes" })
		FieldInjector(Field f, Container container){
			this.f=f;
			if(!f.isAccessible()){
				SecurityManager sm= System.getSecurityManager();
				sm.checkPermission(new ReflectPermission("supressAccessCheck"));
				
				f.setAccessible(true);
			}
			
			if(f.isAnnotationPresent(Value.class)){
				Value value= f.getAnnotation(Value.class);
				//to do
			}
			
			String name= f.getName();
			if(f.isAnnotationPresent(Qualifier.class)){
				Qualifier qualifier= f.getAnnotation(Qualifier.class);
				name= qualifier.value();
			}
			
			Key<?> key= Key.newInstance(f.getClass(), name);
			this.externalContext= new ExternalContext(container, key); 
			factory= container.factories.get(key);
		}
		
		public void inject(InternalContext context, Object instance){
			ExternalContext<?> previous= context.getExternalContext();
			try {
				context.setExternalContext(this.externalContext);
				f.set(instance, factory.create(context));
			} catch (Throwable t) {
				throw new AssertionError(t);
			}finally{
				context.setExternalContext(previous);
			}
		}
	}
	
	class MethodInjector implements Injector{
		@SuppressWarnings("rawtypes")
		ParameterInjector[] parameterInjectors;
		Method m;
		
		MethodInjector(Method m){
			this.m= m;
			
			if(!m.isAccessible()){
				SecurityManager sm= System.getSecurityManager();
				
				if(sm!= null){
					sm.checkPermission(new ReflectPermission("supressAccessChecks"));
				}
				m.setAccessible(true);
			}
			Annotation[][] annss= m.getParameterAnnotations();
			Class<?>[] pts= m.getParameterTypes();
			parameterInjectors= createParameter(annss, pts, m.getDeclaringClass());
		}
		
		public void inject(InternalContext context, Object instance){
			try {
				m.invoke(instance, getParameters(parameterInjectors, context));
			} catch (Throwable e) {
				Throwable t= e.getCause();
				if(t instanceof RuntimeException) throw (RuntimeException)t;
				if(t instanceof Error) throw (Error)t;
				
				throw new AssertionError(e);
			}
		}
	}
	
	interface Injector{
		void inject(InternalContext context, Object instance);
	}
	
	private <T>T getInstance(String name, Class<T> type, InternalContext context){
		Key<T> key= Key.newInstance(type, name);
		InternalFactory<T> factory= factories.get(key);
		ExternalContext<?> previous= context.getExternalContext();
		try{
			
			ExternalContext<T> currentContext= new ExternalContext<T>(this, key);
			context.setExternalContext(currentContext);
			return factory== null? null: factory.create(context);
		}finally{
			context.setExternalContext(previous);
		}
	}
	
	public <T>T getInstance(final String name, final Class<T> type){
		return callInContext(new ContextualCallable<T>(){

			@Override
			public T call(InternalContext context) {
				InternalFactory<T> factory= Container.this.factories.get(Key.newInstance(type, name));
				return getInstance(name, type, context);
			}
			
		});
	}
	
	public <T>T getInstance(Class<T> type){
		return getInstance("default", type);
	}
	
	<T>T callInContext(ContextualCallable<T> callable){
		InternalContext[] reference= localContext.get();
		try{
			if(reference[0]==null){
				reference[0]= new InternalContext(this);
				return callable.call(reference[0]);
			}else{
				return callable.call(reference[0]);
			}
		}finally{
			reference[0]= null;
		}
	}
	
	static interface ContextualCallable<T>{
		
		T call(InternalContext context);
	}
	
}
