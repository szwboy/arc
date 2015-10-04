package arc.ioc.container.classreading.test;

import arc.core.stereotype.Service;

@Service
public class B<A>{
	@Service
	public interface C{void setS(String s);};
	

	int a;
	

	public void setA(int a){
		this.a=a;
	}
}