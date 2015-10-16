package arc.components.factory;

import arc.components.factory.annotation.Inject;
import arc.components.factory.annotation.Qualifier;

public class A {

	private B b;
	@Inject
	public A(@Qualifier("b")B b){
		this.b= b;
	}
	
	public A(){}
	
	public B getB(){
		return b;
	}
}
