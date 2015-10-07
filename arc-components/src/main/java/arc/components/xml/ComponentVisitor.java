package arc.components.xml;


public interface ComponentVisitor {

	<T>void visit(Component<T> component);
}
