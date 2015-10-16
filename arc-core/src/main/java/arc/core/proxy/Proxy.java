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

abstract class Proxy {

	private static final Logger log= Logger.getLogger(Proxy.class);
	// record the proxy classes number
	private static final AtomicLong PROXY_CLASS_COUNTER= new AtomicLong(0);
	// the name of package which include the proxy class
	private static final String PACKAGE_NAME= Proxy.class.getPackage().getName();
	/* store proxy. the key is interfaces list */
	private static final Map<ClassLoader, Map<String, Object>> PROXY_MAP= new WeakHashMap<ClassLoader, Map<String, Object>>();
	/*an object to mark having a thread got the lock*/
	private static final Object pendingObjectMarker= new Object();
	
	private static final Lock lock= new ReentrantLock();
	private static final Condition wait= lock.newCondition();
	/**
	 * generate a proxy object
	 * @param handler
	 * @return
	 */
	abstract Object newInstance(InvocationHandler<?> handler);
	
	/**
	 * get the proxy for specified interfaces
	 * @param ics
	 * @return
	 */
	static Proxy getProxy(Class<?>[] ics){
		return Proxy.getProxy(Proxy.class.getClassLoader(), ics, true);
	}
	
	/**
	 * proxy for class
	 * @param type
	 * @return
	 */
	static Proxy getProxy(Class<?> type){
		return Proxy.getProxy(Proxy.class.getClassLoader(), new Class<?>[]{type}, false);
	}

	/**
	 * get the proxy for specified interfaces
	 * @param cl classlaoder to load the object
	 * @param ics
	 * @return
	 */
	static Proxy getProxy(ClassLoader cl, Class<?>[] ics, boolean isInterfaceProxied) {

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

		synchronized (PROXY_MAP) {
			Map<String, Object> cache = PROXY_MAP.get(cl);
			if (cache == null) {

				cache = new HashMap<String, Object>();
				PROXY_MAP.put(cl, cache);
			}
		}

		try{
			lock.lock();
			do {
				Object value = PROXY_MAP.get(cl).get(key);

				if (value instanceof Reference)
					return (Proxy)((Reference)value).get();

				if (value == null) {
					PROXY_MAP.get(cl).put(key, pendingObjectMarker);
					break;
				} else {
					try {
						wait.await();
					} catch (InterruptedException e) {
					}
				}
			} while (true);
		}finally{
			lock.unlock();
		}

		return isInterfaceProxied?interfaceProxy(ics, cl, key): classProxy(ics[0], cl, key);
	}
	
	
	static Proxy classProxy(Class<?> type, ClassLoader cl, String key){
		String pkg = type.getPackage().getName();

		ClassGenerator cg= ClassGenerator.newInstance(cl);
		List<Method> mms = new ArrayList<Method>();

		cg.setSuperClass(type.getName());
		// ignore repeated method
		Method[] ms = type.getMethods();
		for (int i = 0; i < ms.length; i++) {
			if(ms[i].getDeclaringClass()== Object.class) continue;
			addMethod(ms[i], mms, cg, i);
		}
			
		return getProxy(pkg, cg, mms, cl, key);
	}
	
	/**
	 * proxy for interfaces
	 * @param ics
	 * @param cl
	 * @param key
	 * @return
	 */
	static Proxy interfaceProxy(Class<?>[] ics, ClassLoader cl, String key){
		Set<String> worked = new HashSet<String>();

		ClassGenerator cg= ClassGenerator.newInstance(cl);
		List<Method> mms= new ArrayList<Method>();
		String pkg = null;
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
			Method[] ms = ic.getDeclaredMethods();
			for (int i = 0; i < ms.length; i++) {

				Method m = ms[i];
				String desc = ReflectUtils.desc(m);
				if (worked.contains(desc)) {
					continue;
				}

				worked.add(ReflectUtils.desc(m));
				addMethod(ms[i], mms, cg, i);
			}
		}	

		if(pkg== null) pkg=PACKAGE_NAME;
		return getProxy(pkg, cg, mms, cl, key);
	}
	
	private static Proxy getProxy(String pkg, ClassGenerator cg, List<Method> mms, ClassLoader cl, String key){
		ClassGenerator ccg= null;
		Proxy proxy= null;
		try{
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
		}catch(Throwable t){
			log.error(t);
		}finally{
			if(ccg!= null) ccg.release();
			if(cg!= null) cg.release();
			try{
				lock.lock();
				if(proxy==null) PROXY_MAP.get(cl).remove(key);
				else PROXY_MAP.get(cl).put(key, new WeakReference<Proxy>(proxy));
				wait.signalAll();
			}finally{
				lock.unlock();
			}
		}
		
		return proxy;
	}
	
	private static void addMethod(Method m, List<Method> mms, ClassGenerator cg, int i){
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
	
}