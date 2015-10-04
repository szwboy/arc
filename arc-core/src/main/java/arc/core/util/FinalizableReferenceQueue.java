package arc.core.util;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import org.apache.log4j.Logger;

public class FinalizableReferenceQueue extends ReferenceQueue<Object> {
	private static final Logger log= Logger.getLogger(FinalizableReferenceQueue.class);
	
	private FinalizableReferenceQueue(){}
	
	void start(){
		Thread t= new Thread(new Runnable(){

			@Override
			public void run() {
				try {
					cleanUp(remove());
				} catch (InterruptedException e) {
					log.error("clean up error", e);
				}
			}
			
		});
		
		t.setDaemon(true);
		t.start();
	}
	
	void cleanUp(Reference<?> ref){
		((FinalizableReference)ref).finalizedReferent();
	}
	
	static FinalizableReferenceQueue instance= createAndStart();
	
	static FinalizableReferenceQueue createAndStart(){
		FinalizableReferenceQueue instance= new FinalizableReferenceQueue();
		instance.start();
		return instance;
	}
	
	public static FinalizableReferenceQueue getInstance(){
		return instance;
	}
	
}
