package arc.components.factory;

import arc.components.factory.annotation.Inject;
import arc.components.factory.annotation.Qualifier;

public class B {

	private A a;
	@Inject
	public B(@Qualifier("a")A a){
		this.a= a;
	}
	
	public B(){};
	
	public A getA(){
		return a;
	}
}
