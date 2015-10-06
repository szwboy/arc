package arc.components.factory;

public interface InternalFactory<T> {

	T create(InternalContext context);
}
