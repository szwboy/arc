package arc.ioc.util;

import java.lang.ref.Reference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ReferenceMap<K,V> {

	@SuppressWarnings("rawtypes")
	private ConcurrentHashMap delegate;
	
	private ReferenceType keyRefType;
	private ReferenceType valueRefType;
	
	@SuppressWarnings("rawtypes")
	public ReferenceMap(ReferenceType keyRefType, ReferenceType valueRefType){
		delegate= new ConcurrentHashMap();
		this.keyRefType= keyRefType;
		this.valueRefType= valueRefType;
	}
	
	protected Object referenceKey(K key){
		switch(keyRefType){
			case WEAK: return new WeakKeyReference<Object>(key);
			case SOFT: return new SoftKeyReference<Object>(key);
			default: return key;
		}
	}
	
	protected Object referenceValue(Object key, V value){
		switch(valueRefType){
			case WEAK: return new WeakValueReference<V>(key, value);
			case SOFT: return new SoftValueReference<V>(key, value);
			default: return value;
		}
	}
	
	@SuppressWarnings("unchecked")
	K dereferenceKey(Object reference){
		return (K) dereferenceKey(reference, keyRefType);
	}
	
	@SuppressWarnings("unchecked")
	Object dereferenceKey(Object reference, ReferenceType refType){
		return refType==ReferenceType.STRONG? reference: ((Reference<K>)reference).get();
	}
	
	@SuppressWarnings("unchecked")
	V dereferenceValue(Object reference){
		return (V) dereferenceValue(reference, valueRefType);
	}
	
	@SuppressWarnings("unchecked")
	Object dereferenceValue(Object reference, ReferenceType refType){
		return refType==ReferenceType.STRONG? reference: ((Reference<V>)reference).get();
	}
	
	class SoftKeyReference<T> extends FinalizableSoftReference<T>{   
		public SoftKeyReference(T referent) {
			super(referent);
			this.referent= referent;
		}

		private T referent;

		public void finalizedReferent() {
			delegate.remove(this);
		}

		@Override
		public int hashCode() {
			return System.identityHashCode(referent);
		}

		@Override
		public boolean equals(Object obj) {
			return referenceEquals(this, obj);
		}
		
	}
	
	class SoftValueReference<M> extends FinalizableSoftReference<M>{
		private Object key;
		
		public SoftValueReference(Object key, M referent){
			super(referent);
			this.key= key;
		}

		public void finalizedReferent() {
			delegate.remove(key);
		}
		
		@Override
		public boolean equals(Object obj) {
			return referenceEquals(this, obj);
		}
	}
	
	class WeakKeyReference<T> extends FinalizableWeakReference<T>{   
		private T referent;
		public WeakKeyReference(T referent) {
			super(referent);
			this.referent= referent;
		}


		public void finalizedReferent() {
			delegate.remove(this);
		}
		
		public int hashCode(){
			return System.identityHashCode(referent);
		}
		
		public boolean equals(Object o){
			return referenceEquals(this, o);
		}
		
	}
	
	class WeakValueReference<M> extends FinalizableWeakReference<M>{
		private Object key;
		
		public WeakValueReference(Object key, M referent){
			super(referent);
			this.key= key;
		}

		public void finalizedReferent() {
			delegate.remove(key, this);
		}
		
		public boolean equals(Object o){
			return referenceEquals(this, o);
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private boolean referenceEquals(Reference<?> ref, Object o){
		if(o instanceof Reference){
			
			if(o== ref) return true;
			
			Object referent= ((Reference)o).get();
			return referent!=null&& referent==ref.get();
		}
		
		return ((ReferenceAwareWrapper)o).unWrap()==ref.get();
	}
	
	class ReferenceAwareWrapper{
		
		Object wrapped;
		public ReferenceAwareWrapper(Object wrapped){
			this.wrapped= wrapped;
		}
		
		@Override
		public boolean equals(Object o) {
			return o.equals(this);
		}
		
		public int hashCode(){
			return wrapped.hashCode();
		}
		
		public Object unWrap(){
			return wrapped;
		}
		
	}
	
	class KeyReferenceAwareWrapper extends ReferenceAwareWrapper{
		
		public KeyReferenceAwareWrapper(Object wrapped){
			super(wrapped);
		}
		
		public int hashCode(){
			return System.identityHashCode(wrapped);
		}
	}
	
	Object makeKeyReferenceAware(Object k){
		return keyRefType==ReferenceType.STRONG? k: new KeyReferenceAwareWrapper(k);
	}
	
	Object makeValueReferenceAware(Object v){
		return valueRefType==ReferenceType.STRONG? v: new ReferenceAwareWrapper(v);
	}
	
	public V get(K key){
		ensureNotNull(key);
		Object valueReference= delegate.get(makeKeyReferenceAware(key));
		return valueReference==null? null: dereferenceValue(valueReference);
	}
	
	public V put(K key, V value){
		return execute(PutStrategy.put, key, value);
	}
	
	public V putIfAbsent(K key, V value){
		return execute(PutStrategy.putIfAbsent, key, value);
	}
	
	public V remove(K key){
		Object k= makeKeyReferenceAware(key);
		Object valueReference= delegate.remove(k);
		return dereferenceValue(valueReference);
	}
	
	V execute(Strategy strategy, K key, V value){
		ensureNotNull(key, value);
		Object k= referenceKey(key);
		Object v= referenceValue(k, value);
		Object valueReference= strategy.execute(this, k, v);
		return valueReference== null? null: dereferenceValue(valueReference);
	}
	
	public boolean remove(K key, V value){
		ensureNotNull(key, value);
		Object k= makeKeyReferenceAware(key);
		Object v= makeValueReferenceAware(value);
		return delegate.remove(k,v);
	}
	
	public boolean containsKey(K key){
		ensureNotNull(key);
		Object k= makeKeyReferenceAware(key);
		return delegate.containsKey(k);
	}
	
	public boolean containsValue(V value){
		ensureNotNull(value);
		Object v= makeValueReferenceAware(value);
		return delegate.containsValue(v);
	}
	
	public int size(){
		return delegate.size();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Set<Entry> EntrySet(){
		Set<Map.Entry> entries= delegate.entrySet();
		
		Set<Entry> ens= new HashSet<Entry>(entries.size());
		for(Map.Entry en: entries){
			K keyRef= dereferenceKey(en.getKey());
			V valueRef= dereferenceValue(en.getValue());
			
			Entry entry= new Entry(keyRef, valueRef);
			ens.add(entry);
		}
		
		return ens;
	}
	
	@SuppressWarnings("rawtypes")
	public Set<K> keySet(){
		Set set= delegate.keySet();
		Set<K> keys= new HashSet<K>(set.size());
		for(Object o: set){
			
			keys.add(dereferenceKey(o));
		}
		
		return Collections.unmodifiableSet(keys);
	}
	
	@SuppressWarnings("rawtypes")
	public Collection<V> values(){
		Collection list= delegate.values();
		Collection<V> values= new ArrayList<V>();
		
		for(Object o: list){
			values.add(dereferenceValue(o));
		}
		
		return Collections.unmodifiableCollection(values);
	}
	
	private void ensureNotNull(Object... array){
		
		for(int i=0;i<array.length;i++){
			Object o= array[i];
			if(o== null) throw new NullPointerException("parameter#"+i+" is null");
		}
	}
	
	class Entry implements Map.Entry<K, V>{
		K k;
		V v;
		Entry(K k, V v){
			this.k= k;
			this.v= v;
		}
		
		@Override
		public K getKey() {
			return k;
		}

		@Override
		public V getValue() {
			return v;
		}

		@Override
		public V setValue(V value) {
			this.v= value;
			return put(k, value);
		}
		
	}
	
	interface Strategy{
		@SuppressWarnings("rawtypes")
		Object execute(ReferenceMap referenceMap, Object k, Object v);
	}
	
	protected enum PutStrategy implements Strategy{
		put{

			@SuppressWarnings({ "rawtypes", "unchecked" })
			@Override
			public Object execute(ReferenceMap referenceMap, Object k, Object v) {
				return referenceMap.delegate.put(k, v);
			}
			
		},
		
		putIfAbsent{

			@SuppressWarnings({ "rawtypes", "unchecked" })
			@Override
			public Object execute(ReferenceMap referenceMap, Object k, Object v) {
				return referenceMap.delegate.putIfAbsent(k, v);
			}},
		
		replace{

			@SuppressWarnings({ "rawtypes", "unchecked" })
			@Override
			public Object execute(ReferenceMap referenceMap, Object k, Object v) {
				return referenceMap.delegate.replace(k, v);
			}
				
		};
	}
}
