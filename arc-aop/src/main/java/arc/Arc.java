package com.arc;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

public class Arc<T> {

	private Reader[] readers;
	
	private boolean running;
	private int currentReader;
	
	private List<Work<T>> works;
	
	public Arc(){
		for(int i=0; i<3; i++){
			readers[i]= new Reader("reader "+i);
			readers[i].start();
		}
	}
	
	public void allocation(Request<?> request){
		Reader reader= getReader();
		
		try {
			reader.addRequest(request);
		} catch (InterruptedException e) {
			
		}
	}
	
	private Reader getReader(){
		currentReader= (currentReader+1)%readers.length;
		return readers[currentReader];
	}
	
	class Reader extends Thread{
		String name;
		
		BlockingQueue<Request> requests= new LinkedBlockingQueue<Request>(20);

		public Reader(String name){
			this.name= name;
		}
		
		@Override
		public void run() {
			while(running){
				try {
					final Request request= requests.take();
					
					long reqType= request.getRequestType();
					
					for(final Work<T> work: works){
						
						if(work.support(reqType)){
							Future<?> future= Executors.newCachedThreadPool().submit(new Callable<Object>(){

								@Override
								public Object call() throws Exception {
									return work.service(request);
								}

							});
						}
						
						
					}
					
				} catch (InterruptedException e) {
				} 
			}
		}
		
		public void addRequest(Request e) throws InterruptedException{
			requests.put(e);
		}
	}
	
	public void stop(){
		running= false;
	}
}
