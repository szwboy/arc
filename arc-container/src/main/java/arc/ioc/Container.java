package arc.ioc;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javassist.NotFoundException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import arc.annotation.annotation.Adaptive;
import arc.annotation.annotation.Inject;
import arc.annotation.annotation.Qualifier;
import arc.annotation.annotation.Spi;
import arc.annotation.annotation.Value;
import arc.common.bytecode.ClassGenerator;
import arc.common.proxy.ProxyFactory;
import arc.common.utils.ReflectUtils;

public class Container<T> {
	private static final Logger log= Logger.getLogger(Container.class);
	
	private static final String ARC_DIRECTORY="META-INFO/arc/";

	private final static Map<Class<?>, Container<?>> containers= new ConcurrentHashMap<Class<?>, Container<?>>();
	
	private Class<T> type;
	private Converter conveter;
	private volatile T cachedAdaptive;
	private Class<? extends T> cachedAdaptiveClass;
	private Throwable createAdaptiveError;
	private static final Lock lock= new ReentrantLock();
	
	private Map<String, Class<? extends T>> cachedClasses;
	private Set<Class<? extends T>> cachedWrapperClasses;
	private Map<String, T> cachedInstances;
	
	private boolean creating;
	
	@SuppressWarnings("unchecked")
	public static <T>Container<T> getContainer(Class<T> type){
		if(type==null)
			throw new IllegalArgumentException("Extension type= null");
		if(!type.isInterface())
			throw new IllegalArgumentException("Extension type:"+type.getName()+ " is not interface");
		if(!type.isAnnotationPresent(Spi.class))
			throw new IllegalArgumentException(type.getCanonicalName()+" is not extension since no spi annotation");
		
		if(containers.containsKey(type)) return (Container<T>) containers.get(type);
		
		containers.put(type, new Container<T>(type));
		Container<T> container= (Container<T>) containers.get(type);
		lock.lock();
		try{
			container.conveter= Container.getContainer(Converter.class).getAdaptive();
		}finally{
			lock.unlock();
		}
		
		return container;
	}
	
	private Container(Class<T> type){
		this.type= type;
	}
	
	private T getAdaptive(){
		if(createAdaptiveError== null){
			try{
				if(cachedAdaptive== null){
					cachedAdaptive= createAdaptive();
				}
			}catch(Throwable t){
				createAdaptiveError= t;
				throw new IllegalStateException("failed to get adaptive", t);
			}
		}else throw new IllegalStateException("failed to get adaptive", createAdaptiveError);
		
		return cachedAdaptive;
	}
	
	public T getObject(final String name){
		
		if(creating){
			return createProxy();
		}
		
		if(cachedInstances== null){
			cachedInstances= new HashMap<String, T>();
		}
		if(!cachedInstances.containsKey(name)){ 
			
			cachedInstances.put(name, createObject(name));
			creating= false;
			T t= cachedInstances.get(name);
			inject(t);
			
		}
		
		return cachedInstances.get(name);
	}
	
	private T createProxy(){
		ProxyFactory factory= Container.getContainer(ProxyFactory.class).getAdaptive();
		return factory.getProxy(type);
	}
	
	@SuppressWarnings("unchecked")
	private T createObject(String name){
		
		creating= true;
		Class<? extends T> clz= cachedClasses.get(name.toLowerCase());
		Constructor<? extends T>[] cons= (Constructor<? extends T>[]) clz.getConstructors();
		T t= null;
		
		try{
			for(Constructor<? extends T> con: cons){
			
				if(!con.isAnnotationPresent(Inject.class)) continue;
				
				Class<?>[] pts= con.getParameterTypes();
				Annotation[][] ass= con.getParameterAnnotations();
				Object[] args= new Object[pts.length];
				for(int i=0;i<ass.length;i++){
					Annotation[] as= ass[i];
					String qualifier="";
					String value="";
					for(int j=0;j<as.length;j++){
						Annotation a= as[j];
						
						if(a instanceof Qualifier){
							qualifier= ((Qualifier)a).value();
							break;
						}
						
						if(a instanceof Value){
							value= ((Value)a).value();
							break;
						}
						
					}
					
					if(StringUtils.isBlank(qualifier)&& StringUtils.isBlank(value)){
						try {
							qualifier=ReflectUtils.detectConstructorPn(clz, con.getParameterTypes(), i);
						} catch (NotFoundException e) {
							log.error(e);
						}

						args[i]=Container.getContainer(pts[i]).getObject(qualifier);
					}
					
					if(StringUtils.isNotBlank(value)){
						args[i]= conveter.convert(value, pts[i]);
					}
					
				}
				
				t= con.newInstance(args);
				break;
			}
			t= clz.newInstance();
		}catch(Throwable th){
			throw new IllegalStateException(th);

		}
		return t;
	}
	
	private T createAdaptive() throws InstantiationException, IllegalAccessException{
		Class<? extends T> clz= getAdaptiveClass();
		T adaptive= clz.newInstance();
		return adaptive;
	}
	
	private Class<? extends T> getAdaptiveClass(){
		getClasses();
		
		if(cachedAdaptiveClass!= null){
			return cachedAdaptiveClass;
		}
		
		cachedAdaptiveClass= createAdaptiveClass();
		return cachedAdaptiveClass;
	}
	
	private void getClasses(){
		
		if(cachedClasses== null){
			cachedClasses= new HashMap<String, Class<? extends T>>();
			loadFile(ARC_DIRECTORY);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void loadFile(String dir){
		String file= dir+ type.getName();
		
		Enumeration<URL> urls;
		try {
			ClassLoader cl= Container.class.getClassLoader();

			if(cl!= null){
				urls= cl.getResources(file);
			}else{
				urls= ClassLoader.getSystemResources(file);
			}
			
			if(urls!= null){
				while(urls.hasMoreElements()){
					
					URL url= urls.nextElement();
					BufferedReader reader= new BufferedReader(new InputStreamReader(url.openStream()));
					
					try{
						String line;
						while((line=reader.readLine())!=null){
							
							int ci= line.indexOf('#');
							if(ci== 0) continue;
							
							if(line.length()>0&& line.indexOf('=')> 0){
								
								int index= line.lastIndexOf('=');
								String name= line.substring(0, index);
								String value= line.substring(index+1);
								
								Class<? extends T> clz= (Class<? extends T>) Class.forName(value);
								
								if(!type.isAssignableFrom(clz)){
									throw new IllegalStateException(clz.getName()+" is not subtype of interface:"+type.getName());
								}
								
								if(clz.isAnnotationPresent(Adaptive.class)){
									
									if(cachedAdaptiveClass== null) cachedAdaptiveClass= clz;
									else{
										if(cachedAdaptiveClass!= clz) throw new IllegalStateException("more than 1 adaptive class found for interface:"+type.getName());
									}
								}else{
									try {
										clz.getConstructor(type);
										if(cachedWrapperClasses== null) cachedWrapperClasses= new HashSet<Class<? extends T>>();
	
										cachedWrapperClasses.add(clz);
									} catch (NoSuchMethodException e) {
										
										if(cachedClasses.containsKey(name)){
											if(cachedClasses.get(name)!= clz) throw new IllegalStateException("duplicate extension name:"+name);
										}else{
											cachedClasses.put(name, clz);
										}
									}
								}
							}
						}
					}finally{
						reader.close();
					}
				}
			}
		} catch (Throwable t) {
			log.error("exception occurs when load extension(interface:"+type.getName()+",class file:"+file, t);
		}
	}
	
	private void inject(T instance){
		Class<?> clz= instance.getClass();
		do{
			for(Method m: clz.getDeclaredMethods()){
				if(m.isAnnotationPresent(Inject.class)){
					
					try {
						
						final Class<?>[] pts= m.getParameterTypes();
						Object[] values= new Object[pts.length];
						Annotation[][] ass= m.getParameterAnnotations();
						for(int i=0;i<ass.length;i++){
							Annotation[] as= ass[i];
							String qualifier="";
							String value="";
							for(int j=0;j<as.length;j++){
							
								//if(arg== null) throw new IllegalStateException("the args"+i+" must have value for method:"+clz.getCanonicalName()+"."+m.getName()); 
								
								Annotation a= as[j];
								
								if(a instanceof Qualifier){
									qualifier= ((Qualifier)a).value();
									break;
								}
								
								if(a instanceof Value){
									value= ((Value)a).value();
									break;
								}
							}	
							
							if(StringUtils.isBlank(value)&& StringUtils.isNotBlank(qualifier)) 
								throw new IllegalStateException("method:"+clz.getCanonicalName()+"."+m.getName()+" has both value and ref");
							
							if(StringUtils.isNotBlank(value)){
								values[i]=conveter.convert(value, pts[i]);
							}else{
							
								if(StringUtils.isBlank(qualifier)){
									qualifier= ReflectUtils.detectPn(clz, m.getName(), i);
								}
								
								if(StringUtils.isBlank(qualifier)) throw new IllegalStateException("the value of args"+i+" must have ref for method:"+clz.getCanonicalName()+"."+m.getName());
								
								values[i]= Container.getContainer(pts[i]).getObject(qualifier);
							}
							
						}
						
						m.invoke(instance, values);
						
					} catch (NotFoundException e) {
					} catch (IllegalAccessException e) {
					} catch (IllegalArgumentException e) {
					} catch (InvocationTargetException e) {
					}
				}
			}
			
			for(Field f: clz.getDeclaredFields()){
				
			}
			
			for(Constructor<?> c: clz.getDeclaredConstructors()){
				
			}
			clz= clz.getSuperclass();
		}while(clz!= Object.class);
	}
	
	@SuppressWarnings("unchecked")
	private Class<? extends T> createAdaptiveClass(){
		
		Method[] ms= type.getMethods();
		
		ClassGenerator ac= ClassGenerator.newInstance(Container.class.getClassLoader());
		for(Method m: ms){

			StringBuilder code= new StringBuilder();
			if(!m.isAnnotationPresent(Adaptive.class)){
				code.append("throw new IllegalStateException(\"Unsupport adaptive \");");
			}else{
				String expr= "";
				StringTokenizer token= new StringTokenizer(m.getAnnotation(Adaptive.class).expr(),".");
				
				while(token.hasMoreElements()){
					String ss= token.nextToken();
					expr += "get";
					expr +=Character.toUpperCase(ss.charAt(0))+ ss.substring(1);
					expr +="()";
				}
				int i= m.getAnnotation(Adaptive.class).number();
				
				try {
					code.append("java.lang.String name= ");
					if(i>0&& StringUtils.isNotBlank(expr)) code.append("$"+ i+ "."+ expr);
					else code.append("\""+type.getAnnotation(Spi.class).value()+"\"");
					code.append(';');
					code.append("if("+StringUtils.class.getName()+".isNotBlank(name)){name=Character.toLowerCase(name.charAt(0))+name.substring(1);}");
					code.append("else throw new "+IllegalArgumentException.class.getName()+"(\"args"+i+"."+expr+" is null or spi value is null\");");
					code.append(type.getName()+" ret= null;");
					code.append("try{");
					code.append("ret=("+type.getName()+")arc.ioc.Container.getContainer("+type.getName()+".class).getObject(name);");
					code.append("if(ret== null){throw new "+IllegalArgumentException.class.getName()+"(\"no supported class for \"+name+\",class:"+type.getName()+"\");}");
					code.append("}catch(java.lang.Throwable t){throw new java.lang.RuntimeException(t.getMessage());}");
					if(Void.TYPE!= m.getReturnType()) code.append("return ");

					code.append("ret."+m.getName()+"("); 
					for(int j=0;j<m.getParameterTypes().length;j++){
						code.append("($w)$"+(j+1));
						
						if(j!= m.getParameterTypes().length-1) code.append(",");
					}
					code.append(");");
				} catch (Throwable T) {
				}
			}
			
			ac.addMethod(m.getName(), m.getModifiers(), m.getParameterTypes(), m.getReturnType(), m.getExceptionTypes(), code.toString());
		}
		
		String cln= type.getName()+"$Adaptive";
		ac.setClassName(cln);
		ac.addInterface(type.getName());
		ac.addDefaultConstructor();
		
		Class<? extends T> cc= (Class<? extends T>) ac.toCtClass();
		return cc;
	}
	
}
