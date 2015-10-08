package arc.core.spi;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import arc.core.bytecode.ClassGenerator;
import arc.core.proxy.ProxyFactory;
import arc.core.spi.annotation.Adaptive;
import arc.core.spi.annotation.Spi;

public class ServiceLoader<T> {
	private static final Logger log= Logger.getLogger(ServiceLoader.class);
	
	/*spi directory*/
	private static final String ARC_DIRECTORY="META-INF/arc/";
	/*cache all service loaders*/
	private final static Map<Class<?>, ServiceLoader<?>> SPI_CACHE= new ConcurrentHashMap<Class<?>, ServiceLoader<?>>();
	/*service interface type*/
	private Class<T> type;
	/*cached adaptive instance*/
	private volatile T cachedAdaptive;
	/*cached adaptive class*/
	private Class<? extends T> cachedAdaptiveClass;
	/*adaptive creation error*/
	private Throwable createAdaptiveError;
	/*map name with provider class*/
	private Map<String, Class<? extends T>> cachedClasses;
	/*adaptive wrapper*/
	private Set<Class<? extends T>> cachedWrapperClasses;
	/*map name with provider instance*/
	private Map<String, T> cachedInstances;
	/*adaptive is in creation*/
	private boolean creating;
	/*factory to get bean of dependencies*/
	private DependencyFactory dependencyFactory;
	
	/**
	 * get service loader for a specified service interface type
	 * @param type
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T>ServiceLoader<T> getLoader(Class<T> type){
		if(type==null)
			throw new IllegalArgumentException("Extension type= null");
		if(!type.isInterface())
			throw new IllegalArgumentException("Extension type:"+type.getName()+ " is not interface");
		if(!type.isAnnotationPresent(Spi.class))
			throw new IllegalArgumentException(type.getCanonicalName()+" is not extension since no spi annotation");
		
		if(SPI_CACHE.containsKey(type)) return (ServiceLoader<T>) SPI_CACHE.get(type);
		
		SPI_CACHE.put(type, new ServiceLoader<T>(type));
		ServiceLoader<T> loader= (ServiceLoader<T>) SPI_CACHE.get(type);
		
		return loader;
	}
	
	private ServiceLoader(Class<T> type){
		this.type= type;
		this.dependencyFactory= type!= DependencyFactory.class?ServiceLoader.getLoader(DependencyFactory.class).getAdaptiveProvider(): null;
	}
	
	/**
	 * get adaptive instance for specified service interface
	 * @return
	 */
	public T getAdaptiveProvider(){
		if(createAdaptiveError== null){
			try{
				if(cachedAdaptive== null){
					synchronized(cachedAdaptive){
						cachedAdaptive= createAdaptive();
					}
				}
			}catch(Throwable t){
				createAdaptiveError= t;
				throw new IllegalStateException("failed to get adaptive", t);
			}
		}else throw new IllegalStateException("failed to get adaptive", createAdaptiveError);
		
		return cachedAdaptive;
	}
	
	/**
	 * get specified provider
	 * @param name
	 * @return
	 */
	public T getProvider(String name){
		if(!cachedInstances.containsKey(name)){
			synchronized(cachedInstances){
				if(!cachedClasses.containsKey(name)){
					getClasses();
				}
				
				Class<? extends T> impl= cachedClasses.get(name);
				try {
					cachedInstances.put(name, type.cast(impl.newInstance()));
				} catch (InstantiationException | IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			}
			
		}
		return cachedInstances.get(name);
	}
	
	private T createProxy(){
		ProxyFactory factory= ServiceLoader.getLoader(ProxyFactory.class).getAdaptiveProvider();
		return factory.getProxy(type);
	}
	
	private T createAdaptive() throws InstantiationException, IllegalAccessException{
		Class<? extends T> type= getAdaptiveClass();
		T adaptive= inject(type);
		return adaptive;
	}
	
	private T inject(Class<? extends T> type){
		return dependencyFactory.depend(type);
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
			synchronized(cachedClasses){
				cachedClasses= new HashMap<String, Class<? extends T>>();
				loadFile(ARC_DIRECTORY);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void loadFile(String dir){
		String file= dir+ type.getName();
		
		Enumeration<URL> urls;
		try {
			ClassLoader cl= ServiceLoader.class.getClassLoader();

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
	
	@SuppressWarnings("unchecked")
	private Class<? extends T> createAdaptiveClass(){
		
		Method[] ms= type.getMethods();
		
		ClassGenerator ac= ClassGenerator.newInstance(ServiceLoader.class.getClassLoader());
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
					code.append("ret=("+type.getName()+")arc.core.spi.ServiceLoader.getLoader("+type.getName()+".class).getObject(name);");
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
