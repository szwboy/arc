package arc.context.aop;

import java.util.concurrent.atomic.AtomicInteger;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.szw.Ordered;
import com.szw.aop.Advice;
import com.szw.aop.Advisor;
import com.szw.aop.AopAutoCreator;
import com.szw.aop.AssistAfterReturningAdvice;
import com.szw.aop.AssistAfterThrowingAdvice;
import com.szw.aop.AssistIntroductionAdvice;
import com.szw.aop.AssistMethodBeforeAdvice;
import com.szw.aop.Pointcut;
import com.szw.aop.RegexAdvisor;
import com.szw.aop.RegexPointcut;
import com.szw.ioc.ContainerPostProcessor;
import com.szw.ioc.Context;
import com.szw.ioc.Factory;
import com.szw.ioc.Scope;
import com.szw.xml.ConfigParser;
import com.szw.xml.Configuration;
import com.szw.xml.ParserContext;


public class AopConfigParser implements ConfigParser {
	private AtomicInteger atomicInteger=new AtomicInteger(0);
	private volatile int count;

	@Override
	public Configuration parser(Element e, ParserContext parserContext) {
		if(count++==0)
			configAutoProxyCreator(parserContext);
		
		if(parserContext.getParser().isNodeEquals("aspect",e)){
			String ref=e.getAttribute("ref");
			
			NodeList nodes=e.getChildNodes();
			for(int i=0;i<nodes.getLength();i++){
				Node node=nodes.item(i);
				if(node instanceof Element){
					
					if(parserContext.getParser().isNodeEquals("before",(Element)node)
							||parserContext.getParser().isNodeEquals("after-throwing",(Element)node)
							||parserContext.getParser().isNodeEquals("after-returning",(Element)node)){
						Configuration config = parseAdvice((Element)node,ref);
						parserContext.getReaderContext().getBuilder().factory(config.getClazz(), config.getName(), new AopConfigFactory(config), Scope.SINGLETON,Ordered.ADVISOR_ORDER);
					}
				}
			}
		}else if(parserContext.getParser().isNodeEquals("pointcut",e)){
			parsePointcut(e,parserContext);
		}else if(parserContext.getParser().isNodeEquals("introduction",e)){
			NodeList nodes=e.getChildNodes();
			for(int i=0;i<nodes.getLength();i++){
				if(nodes.item(i) instanceof Element&&parserContext.getParser().isNodeEquals("interfaces",(Element)nodes.item(i))){
					Element ele=(Element)nodes.item(i);
					Configuration config=parseIntroduction(ele,parserContext,e);
					parserContext.getReaderContext().getBuilder().factory(config.getClazz(), config.getName(), new AopConfigFactory(config), Scope.SINGLETON,Ordered.ADVISOR_ORDER);
				}
			}
		}

		return null;
	}
	
	private void parsePointcut(Element pointcutEle,ParserContext parserContext){
		Pointcut pointcut=new RegexPointcut(pointcutEle.getAttribute("expression"));
		Configuration config=new Configuration("com.szw.aop.Pointcut");
		config.setValue(pointcut);
		config.setName(pointcutEle.getAttribute("name"));
		parserContext.getReaderContext().getBuilder().factory(config.getClazz(), config.getName(), new AopConfigFactory(config), Scope.SINGLETON,Ordered.POINTCUT_ORDER);
	}
	
	private Configuration parseAdvice(Element e,String aspectName){
		String methodName=e.getAttribute("method");
		String refArgs=e.getAttribute("ref-args");
		
		Advice advice=null;
		if(e.getLocalName().equals("before"))
			advice=new AssistMethodBeforeAdvice(methodName,aspectName,refArgs);
		else if(e.getLocalName().equals("after-throwing")){
			advice=new AssistAfterThrowingAdvice(methodName,aspectName,e.getAttribute("throwing"));
		}else if(e.getLocalName().equals("after-returning")){
			advice=new AssistAfterReturningAdvice(methodName,aspectName,refArgs);
		}
		
		Advisor advisor=null;
		if(e.hasAttribute("pointcut")&&e.hasAttribute("pointcut-ref"))
			throw new RuntimeException("Either pointcut or pointcut-ref can be existed");
		
		if(e.hasAttribute("pointcut")){
			advisor=new RegexAdvisor(new RegexPointcut(e.getAttribute("pointcut")),advice);
		}else{
			advisor=new RegexAdvisor(e.getAttribute("pointcut-ref"),advice);
		}
		
		Configuration config=new Configuration("com.szw.aop.Advisor");
		config.setValue(advisor);
		config.setName("advisor"+atomicInteger.getAndIncrement());
		return config;
	}
	
	private Configuration parseIntroduction(Element e,ParserContext parserContext,Element parentEle){
		String ifc=e.getAttribute("interface");
		String impl=e.getAttribute("impl");
		Advice advice=new AssistIntroductionAdvice(ifc,impl);
		
		
		Advisor advisor=null;
		if(e.hasAttribute("pointcut")&&e.hasAttribute("pointcut-ref"))
			throw new RuntimeException("Either pointcut or pointcut-ref can be existed");
		
		if(parentEle.hasAttribute("pointcut")){
			advisor=new RegexAdvisor(new RegexPointcut(parentEle.getAttribute("pointcut")),advice);
		}else{
			advisor=new RegexAdvisor(parentEle.getAttribute("pointcut-ref"),advice);
		}
		
		Configuration config=new Configuration("com.szw.aop.Advisor");
		config.setValue(advisor);
		config.setName("advisor"+atomicInteger.getAndIncrement());
		return config;
	}
	
	static class AopConfigFactory implements Factory<Object>{
		Configuration config;
		
		AopConfigFactory(Configuration config){
			this.config=config;
		}
		
		@Override
		public Object create(Context context) {
			Object o=config.getValue();
			
			if(o instanceof Advisor){
				context.getContainer().inject(o);
				
				Advice advice=((Advisor)o).getAdvice();
				context.getContainer().inject(advice);
			}
			
			return config.getValue();
		}
		
	}
	
	private void configAutoProxyCreator(ParserContext parserContext){
		parserContext.getReaderContext().getBuilder().factory(ContainerPostProcessor.class, "containerPostProcessor", new Factory<ContainerPostProcessor>(){
			ContainerPostProcessor processor;
			
			public ContainerPostProcessor create(Context context){
				if(processor==null) 
					processor=context.getContainer().inject(AopAutoCreator.class);
				
				return processor;
			}
			
		}, Scope.SINGLETON,Ordered.CONTAINERPOSTPROCESSOR_ORDER);
		
	}
	
}
