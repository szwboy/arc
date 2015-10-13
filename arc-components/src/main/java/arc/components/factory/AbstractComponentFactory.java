package arc.components.factory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.ReflectPermission;
import java.util.ArrayList;
import java.util.List;

import javassist.NotFoundException;

import org.apache.commons.lang.StringUtils;

import arc.components.factory.annotation.Inject;
import arc.components.factory.annotation.Qualifier;
import arc.components.factory.annotation.Value;
import arc.components.support.DependencyInjector;
import arc.core.cache.ReferenceCache;
import arc.core.proxy.InvocationHandler;
import arc.core.proxy.ProxyFactory;
import arc.core.spi.ServiceLoader;
import arc.core.util.ReflectUtils;

/**
 * containing all components
 * @author sunzhongwei
 *
 */
abstract class AbstractComponentFactory implements ComponentFactory, DependencyInjector{


	/*store internal context for current creating bean*/
	private ThreadLocal<InternalContext[]> localContext= new ThreadLocal<InternalContext[]>(){

		@Override
		protected InternalContext[] initialValue() {
			return new InternalContext[1];
		}

	};
	
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
				parameterInjectors[i]= new ParameterInjector(context, getFactory(key));
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
	
	class InjectorInvocationHandler<T> implements InvocationHandler<T>{

		T t;
		@Override
		public Object invoke(Method method, Object[] args, Object proxy) {
			
			if(t== null) throw new IllegalStateException("instance of "+t.getClass().getName()+" is null");
			try {
				return method.invoke(t, args);
			} catch (IllegalAccessException | IllegalArgumentException e) {
				throw new RuntimeException(e);
			} catch (InvocationTargetException e) {
				throw new RuntimeException(e.getTargetException());
			} 
		}
		
		public void setObject(T t){
			this.t= t;
		}
		
	}
	
	public class ConstructorInjector<T>{
		ParameterInjector<?>[] parameterInjectors;
		Constructor<? extends T> constructor;
		List<Injector> injectors;
		boolean creating;
		T proxy;
		T t;
		InvocationHandler<T> handler;
		
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
		
		T construct(InternalContext context, Class<T> expectedType){
			if(creating){
				
				if(handler== null){
					synchronized(this){
						if(handler== null){
							handler= new InjectorInvocationHandler<T>();
							proxy= createInjectorProxy(expectedType, handler);
						}
					}
				}
				
				return proxy;
			}
			
			if(t!= null) return t;
			try {
				creating= true;
				Object[] parameters= getParameters(parameterInjectors, context);
				try{
					t= constructor.newInstance(parameters);
					handler.setObject(t);
				}finally{
					creating= false;
				}
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
	
	private <T>T createInjectorProxy(Class<T> type, InvocationHandler<T> handler){
		ProxyFactory proxyFactory= ServiceLoader.getLoader(ProxyFactory.class).getAdaptiveProvider();
		proxyFactory.setHandler(handler);
		return proxyFactory.getProxy(type);
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
		FieldInjector(Field f, ComponentFactory componentFactory){
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
			this.externalContext= new ExternalContext(componentFactory, key); 
			factory= ((AbstractComponentFactory)componentFactory).getFactory(key);
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
	
	/**
	 * injector of a member
	 * @author sunzhongwei
	 *
	 */
	interface Injector{
		void inject(InternalContext context, Object instance);
	}
	
	/**
	 * It has a callback method,which would create a internalcontext
	 * to keep the context of this component
	 * @author sunzhongwei
	 * @param callable
	 * @return
	 */
	public <T>T callInContext(ContextualCallable<T> callable){
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
	
	/**
	 * context of dependency. 
	 *
	 * @param <T>
	 */
	public static interface ContextualCallable<T>{
		
		T call(InternalContext context);
	}
	
	protected abstract <T>InternalFactory<T> getFactory(Key<T> key);
	
	private <T>T getComponent(String name, Class<T> type, InternalContext context){
		Key<T> key= Key.newInstance(type, name);
		InternalFactory<T> factory= getFactory(key);
		ExternalContext<?> previous= context.getExternalContext();
		try{
			
			ExternalContext<T> currentContext= new ExternalContext<T>(this, key);
			context.setExternalContext(currentContext);
			return factory== null? null: factory.create(context);
		}finally{
			context.setExternalContext(previous);
		}
	}
	
	/**==============================================================
	 * implementation of {@link ComponentFactory}
	 *=============================================================*/
	public <T>T getComponent(final String name, final Class<T> type){
		return callInContext(new ContextualCallable<T>(){

			@Override
			public T call(InternalContext context) {
				return getComponent(name, type, context);
			}
			
		});
	}
	
	public <T>T getComponent(Class<T> type){
		return getComponent("default", type);
	}
	
	/**=================================================
	 * implementation of {@link DependencyInjector}
	 *================================================*/
	public <T>T inject(final Class<T> type){
		return callInContext(new ContextualCallable<T>(){

			@Override
			public T call(InternalContext context) {
				return getConstructor(type).construct(context, type);
			}
			
		});
	}
	
	public <T>void inject(final T t){
		callInContext(new ContextualCallable<T>(){

			@Override
			public T call(InternalContext context) {
				AbstractComponentFactory componentFactory= (AbstractComponentFactory) context.getComponentFactory();
				List<Injector> injectors= componentFactory.getConstructor(t.getClass()).injectors;
				
				for(Injector injector: injectors){
					injector.inject(context, t);
				}
				return t;
			}
			
		});
	}
	
}
