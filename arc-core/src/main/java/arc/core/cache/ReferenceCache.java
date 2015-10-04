package arc.core.cache;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import arc.core.util.ReferenceMap;
import arc.core.util.ReferenceType;

public abstract class ReferenceCache<K, V> extends ReferenceMap<K, V> {

	private ConcurrentHashMap<K, Future<V>> futures;
	private ThreadLocal<Future<V>> localFuture;

	public ReferenceCache(ReferenceType keyRefType, ReferenceType valueRefType) {
		super(keyRefType, valueRefType);
		this.futures= new ConcurrentHashMap<K, Future<V>>();
		this.localFuture= new ThreadLocal<Future<V>>();
	}
	
	public ReferenceCache(){
		super(ReferenceType.STRONG, ReferenceType.STRONG);
	}
	
	protected abstract V doCreate(K key);
	
	public V create(final K key){
		try {
			FutureTask<V> futureTask= new FutureTask<V>(new Callable<V>(){
				public V call() throws Exception {
					
					V value;
					if((value=ReferenceCache.this.get(key))== null){
						value= doCreate(key);
					}
					return value;
				}
				
			});
		
			try{
				Future<V> future= futures.putIfAbsent(key, futureTask);
				if(future== null){
					
					if(localFuture.get()!= null){
						throw new IllegalStateException("Nested creation within the same cache are not allowed");
					}
					
					localFuture.set(futureTask);
					futureTask.run();
					V value= futureTask.get();
					Object refKey= referenceKey(key);
					PutStrategy.put.execute(this, refKey, referenceValue(refKey, value));
					return value;
				}else{
					return future.get();
				}
			}finally{
				futures.remove(key);
				localFuture.remove();
			}
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} catch (ExecutionException e) {
			Throwable t= e.getCause();
			if(t instanceof RuntimeException) throw (RuntimeException)t;
			if(t instanceof Error) throw (Error)t;
			
			throw new RuntimeException(e);
		}
	}
	
	public void cancel(){
		Future<V> future= localFuture.get();
		
		if(future== null){
			throw new IllegalStateException("not in creation");
		}
		future.cancel(false);
	}
	
	public V get(K key){
		V v= super.get(key);
		
		return v== null? create(key): v;
	}
}
