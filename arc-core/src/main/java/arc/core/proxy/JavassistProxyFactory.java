package arc.core.proxy;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

import arc.core.bytecode.ClassGenerator;
import arc.core.util.ReflectUtils;

public class JavassistProxyFactory extends AbstractProxyFactory {
	
	@SuppressWarnings("unchecked")
	@Override
	protected <T>T doProxy(Class<T> ifc){
		Class<? super T>[] ics= ReflectUtils.getAllInterfaces(ifc);
		return (T) Proxy.getProxy(ics).newInstance(getHandler());
	}
	
	protected abstract static class Proxy {

		private static final Logger log = Logger.getLogger(Proxy.class);

		// record the proxy classes number
		private static final AtomicLong PROXY_CLASS_COUNTER = new AtomicLong(0);
		// the name of package which include the proxy class
		private static final String PACKAGE_NAME = Proxy.class.getPackage().getName();

		/* store proxy. the key is interfaces list */
		private static final Map<String, Map<String, Object>> PROXY_MAP = new WeakHashMap<String, Map<String, Object>>();

		private static final Lock lock = new ReentrantLock();
		private static final Condition wait = lock.newCondition();
		private static final Object pendingObjectMarker = new Object();

		abstract Object newInstance(InvocationHandler handler);
		
		static Proxy getProxy(Class<?>[] ics){
			return Proxy.getProxy(Proxy.class.getClassLoader(), ics);
		}

		static Proxy getProxy(ClassLoader cl, Class<?>[] ics) {

			// the key in the PROXY_MAP is
			// interfacename1+','+....+interfacenamen
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < ics.length; i++) {

				Class<?> tmp = null;
				try {
					tmp = Class.forName(ics[i].getName(), false, cl);
				} catch (ClassNotFoundException e) {
					log.error(e);
				}

				if (tmp != ics[i])
					throw new RuntimeException(ics[i].getName()
							+ " is not visible to the classloader");

				sb.append(ics[i].getName());
				if (i != ics.length - 1)
					sb.append(",");
			}

			String key = sb.toString();

			Map<String, Object> cache = null;
			synchronized (PROXY_MAP) {
				cache = PROXY_MAP.get(cl);
				if (cache == null) {

					cache = new HashMap<String, Object>();
					PROXY_MAP.put(key, cache);
				}
			}

			lock.lock();
			do {
				Object value = cache.get(key);

				if (value instanceof Reference)
					return (Proxy) ((Reference)value).get();

				if (value == null) {
					cache.put(key, pendingObjectMarker);
					break;
				} else {
					try {
						wait.await();
					} catch (InterruptedException e) {
					}
				}
			} while (true);
			lock.unlock();

			Proxy proxy= null;
			ClassGenerator ccg= null;
			ClassGenerator cg= null;
			try {
				Set<String> worked = new HashSet<String>();
				String pkg = null;

				cg= ClassGenerator.newInstance(cl);
				List<Method> mms = new ArrayList<Method>();
				for (Class<?> ic : ics) {

					String iPkg = ic.getPackage().getName();

					if (!Modifier.isPublic(ic.getModifiers())) {

						// we should make sure all non-public interfaces in the
						// same package
						if (pkg == null) {
							pkg = iPkg;
						} else if (!pkg.equals(iPkg))
							throw new RuntimeException(
									"non-public interfaces must be under same package");
					}

					cg.addInterface(ic.getName());
					// ignore repeated method
					Method[] ms = ic.getMethods();
					for (int i = 0; i < ms.length; i++) {

						Method m = ms[i];
						String desc = ReflectUtils.desc(m);
						if (worked.contains(desc)) {
							continue;
						}

						worked.add(ReflectUtils.desc(m));

						StringBuilder code = new StringBuilder();

						Class<?>[] pts = m.getParameterTypes();
						// assign value to args
						code.append("Object[] args= new Object [").append(pts.length).append(']').append(';');

						for (int j = 0; j < pts.length; j++) {
							code.append("args[").append(i).append("]=($w)$").append(j + 1);

							if (j != pts.length - 1)
								code.append(';');
						}

						// proxy the method invoking
						code.append("Object ret=handler.invoke(mns[").append(i).append("],args,this);");

						if (m.getReturnType() != void.class) {
							
							code.append("return ("+ReflectUtils.getName(m.getReturnType())+")ret;");
						}

						mms.add(m);

						cg.addMethod(m.getName(), m.getModifiers(), m.getParameterTypes(), m.getReturnType(), m.getExceptionTypes(), code.toString());
					}
					
					if(pkg== null) pkg=PACKAGE_NAME;
					
					long id= PROXY_CLASS_COUNTER.getAndIncrement();
					//craete class proxied
					String cn= pkg+".proxy"+id;
					cg.setClassName(cn);
					cg.addField("public static java.lang.reflect.Method[] mns;");
					cg.addField("private "+InvocationHandler.class.getName()+" handler;");
					cg.addConstructor(Modifier.PUBLIC, "handler=$1;", new Class<?>[0], new Class[]{InvocationHandler.class});
					Class<?> clazz = cg.toCtClass();
					clazz.getFields();
					clazz.getField("mns").set(null, mms.toArray(new Method[0]));
					
					//create proxy class
					String proxycn=Proxy.class.getName()+id;
					ccg= ClassGenerator.newInstance(cl);
					ccg.setClassName(proxycn);
					ccg.addDefaultConstructor();
					ccg.setSuperClass(Proxy.class.getName());
					ccg.addMethod("public Object newInstance("+InvocationHandler.class.getName()+" h){return new "+cn+ "(h);}");
					
					proxy= (Proxy) ccg.toCtClass().newInstance();
					
				}
			} catch (Exception e) {
				log.error("error",e);
			}finally{
				
				ccg.release();
				cg.release();
				
				lock.lock();
				if(proxy==null) cache.remove(key);
				else cache.put(key, new WeakReference<Proxy>(proxy));
				wait.signalAll();
				lock.unlock();
			}
			
			return proxy;
		}
		
	}

}
