package arc.aop.autoproxy;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import arc.aop.Advisor;
import arc.components.factory.RegistrableComponentFactory;

public class AdvisorRetrievalHelper {

	private RegistrableComponentFactory componentFactory;
	
	public AdvisorRetrievalHelper(RegistrableComponentFactory componentFactory){
		this.componentFactory= componentFactory;
	}
	
	@SuppressWarnings("unchecked")
	public List<Advisor> findAdvisors(){
		Set<String> componentNames= componentFactory.getComponentNames(Advisor.class);
		
		if(componentNames== null){
			return null;
		}
		
		List<Advisor> advisors= new LinkedList<Advisor>();
		for(String componentName: componentNames){
			advisors.add(componentFactory.getComponent(componentName, Advisor.class));
		}
		
		return advisors;
	}
	
	protected boolean isEligible(String componentName){
		return true;
	}
}
