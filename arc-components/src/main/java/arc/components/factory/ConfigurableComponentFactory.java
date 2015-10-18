package arc.components.factory;

/**
 * component factory which can be configured such as component post processor and so on
 * @author sunzhongwei
 *
 */
public interface ConfigurableComponentFactory extends ComponentFactory {

	void addComponentPostProcessor(ComponentPostProcessor<?> componentPostProcessor);
}
