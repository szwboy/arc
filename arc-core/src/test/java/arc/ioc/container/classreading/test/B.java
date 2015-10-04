package arc.ioc.container.classreading.test;

import arc.annotation.annotation.Qualifier;
import arc.annotation.annotation.Service;
import arc.annotation.annotation.Spi;

@Spi(value="a")
public class B<A>{
	@Service
	public interface C{void setS(String s);};
	
	@Qualifier("aaaa")
	int a;
	
	@Qualifier("aaaa")
	public void setA(@Qualifier("aaaa")int a){
		this.a=a;
	}
}