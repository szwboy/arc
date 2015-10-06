package arc.components.support;

import arc.components.factory.InternalContext;
import arc.components.factory.InternalFactory;
import arc.components.factory.Key;

public enum Scope {
	Default{

		@Override
		public <T> InternalFactory<T> scopeFactory(Key<T> key,
				InternalFactory<T> factory) {
			return factory;
		}
		
	},

	Thread{

		@Override
		public <T> InternalFactory<T> scopeFactory(Key<T> key,
				final InternalFactory<T> factory) {
			return new InternalFactory<T>(){
				ThreadLocal<T> local= new ThreadLocal<T>();
				public T create(InternalContext context){
					
					if(local.get()== null){
						local.set(factory.create(context));
					}
					
					return local.get();
				}
				
			};
		}
		
	},
	
	Singleton{

		@Override
		public <T> InternalFactory<T> scopeFactory(Key<T> key,
				final InternalFactory<T> factory) {
			return new InternalFactory<T>(){

				T instance;
				public T create(InternalContext context){
					if(instance== null){
						instance= factory.create(context);
					}
					
					return instance;
				}
				
			};
		}
		
	};
	
	public abstract <T>InternalFactory<T> scopeFactory(Key<T> key, InternalFactory<T> factory);
}
