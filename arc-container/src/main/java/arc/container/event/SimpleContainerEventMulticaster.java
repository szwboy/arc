package arc.container.event;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import arc.components.factory.ComponentFactory;
import arc.components.factory.Key;

public class SimpleContainerEventMulticaster implements ContaienrEventMulticaster {
	private ComponentFactory componentFactory;
	private ListenerRetriever defaultRetriever= new ListenerRetriever();
	private Map<ListenerCacheKey, ListenerRetriever> listenerRetrivers= new HashMap<ListenerCacheKey, ListenerRetriever>();
	
	public SimpleContainerEventMulticaster(ComponentFactory componentFactory){
		synchronized(defaultRetriever){
			this.componentFactory= componentFactory;
		}
	}

	@Override
	public void addListener(ContainerListener<ContainerEvent> listener) {
		defaultRetriever.containerListeners.add(listener);
	}

	@Override
	public void removeListener(ContainerListener<ContainerEvent> listener) {
		defaultRetriever.containerListeners.remove(listener);
	}

	@Override
	public void addListenerComponentName(String name) {
		defaultRetriever.containerListenerBeans.add(name);
	}

	@Override
	public void removeListenerComponentName(String name) {
		defaultRetriever.containerListenerBeans.remove(name);
	}

	@Override
	public void multicastEvent(ContainerEvent eve) {
		List<ContainerListener<ContainerEvent>> containerListeners= getContainerListeners(eve);
		for(ContainerListener<ContainerEvent> listener: containerListeners){
			listener.onContainerEvent(eve);
		}
	}
	
	private List<ContainerListener<ContainerEvent>> getContainerListeners(ContainerEvent event){
		Object source= event.getSource();
		Class<?> sourceType= source.getClass();
		Class<?> eventType= event.getClass();
		
		ListenerCacheKey cacheKey= new ListenerCacheKey(sourceType, eventType);
		if(listenerRetrivers.containsKey(cacheKey)) return listenerRetrivers.get(cacheKey).getContainerListeners();
		
		synchronized(listenerRetrivers){
			if(listenerRetrivers.containsKey(cacheKey)) return listenerRetrivers.get(cacheKey).getContainerListeners();
				
			ListenerRetriever retriever= new ListenerRetriever();
			listenerRetrivers.put(cacheKey, retriever);
			return retrieveContainerListeners(sourceType, eventType, retriever);
		}
	}
	@SuppressWarnings("rawtypes")
	private List<ContainerListener<ContainerEvent>> retrieveContainerListeners(Class<?> sourceType, 
			Class<?> eventType, ListenerRetriever retriever){
		List<ContainerListener<ContainerEvent>> allListeners= new LinkedList<ContainerListener<ContainerEvent>>();
		
		Set<ContainerListener<ContainerEvent>> listeners;
		Set<String> listenerBeans;
		synchronized(listenerRetrivers){
			listeners= new LinkedHashSet<ContainerListener<ContainerEvent>>(defaultRetriever.containerListeners);
			listenerBeans= new LinkedHashSet<String>(defaultRetriever.containerListenerBeans);
		}
		
		for(ContainerListener<ContainerEvent> listener: listeners){
			if(supportEvent(listener, eventType, sourceType)){
				allListeners.add(listener);
				
				if(retriever!= null){
					retriever.containerListeners.add(listener);
				}
			}
		}
		
		for(String name: listenerBeans){
			ContainerListener listener= componentFactory.getComponent(name, ContainerListener.class);
			if(supportEvent(listener, eventType, sourceType)&& !allListeners.contains(listener)){
				allListeners.add(listener);
				
				if(retriever!= null){
					retriever.containerListenerBeans.add(name);
				}
			}
		}
		
		return allListeners;
	}
	
	private boolean supportEvent(ContainerListener<ContainerEvent> listener, Class<?> eventType, Class<?> sourcType){
		
		return true;
	}
	
	private class ListenerCacheKey{
		Class<?> sourceType;
		Class<?> eventType;
		
		ListenerCacheKey(Class<?> sourceType, Class<?> eventType){
			this.sourceType= sourceType;
			this.eventType= eventType;
		}

		@Override
		public int hashCode() {
			return sourceType.hashCode()^eventType.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if(!(obj instanceof ListenerCacheKey)) return false;
			
			ListenerCacheKey cache= (ListenerCacheKey)obj;
			if(cache.eventType== this.eventType&& this.sourceType== cache.sourceType) return true;
			
			return false;
		}
		
	}
	
	private class ListenerRetriever{
		Set<ContainerListener<ContainerEvent>> containerListeners;
		Set<String> containerListenerBeans;
		
		ListenerRetriever(){
			this.containerListeners= new HashSet<ContainerListener<ContainerEvent>>();
			this.containerListenerBeans= new HashSet<String>();
		}
		
		@SuppressWarnings("rawtypes")
		List<ContainerListener<ContainerEvent>> getContainerListeners(){
			
			List<ContainerListener<ContainerEvent>> allListeners= new LinkedList<ContainerListener<ContainerEvent>>();
			for(ContainerListener<ContainerEvent> containerListener: containerListeners){
				allListeners.add(containerListener);
			}
			
			for(String name: containerListenerBeans){
				ContainerListener listener= componentFactory.getComponent(name, ContainerListener.class);
				allListeners.add(listener);
			}
			
			return allListeners;
		}
		
	}

}
