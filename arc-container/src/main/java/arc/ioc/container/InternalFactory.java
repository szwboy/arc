package arc.ioc.container;

public interface InternalFactory<T> {

	T create(InternalContext context);
}
