package arc.components.factory;

import arc.components.support.Scope;

public interface InternalFactory<T> {

	T create(InternalContext context);
	
	Class<T> getType();
	
	Scope getScope();
}
