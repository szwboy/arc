package arc.components.xml;


public class SynchronizedTest {

	private String lock1="lock1";
	private String lock2="lock2";
	
	public static void main(String[] args){
		final SynchronizedTest test= new SynchronizedTest();
		for(int i=0;i<100;i++){
			Thread t= new Thread(new Runnable(){

				@Override
				public void run() {
					test.setA1();
					test.setB1();
				}
				
			});
			t.start();
		}
	}
	
	private void setA1(){
		synchronized(lock1){
			System.out.println("lockA1");
		}
	}
	
	private void setB1(){
		synchronized(lock1){
			System.out.println("lockB1");
		}
	}
	
	private void setA2(){
		synchronized(lock2){
			System.out.println("lockA2");
		}
	}
	
	private void setB2(){
		synchronized(lock2){
			System.out.println("lockB2");
		}
	}
}
