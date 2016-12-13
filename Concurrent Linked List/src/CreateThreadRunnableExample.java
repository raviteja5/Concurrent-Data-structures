import java.util.concurrent.Semaphore;
import java.util.*;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;


public class CreateThreadRunnableExample implements Runnable {
	FineGrainedHeap cl=new FineGrainedHeap(100);                    //method to implement
	final CyclicBarrier gate = new CyclicBarrier(10);
	Semaphore mn=new Semaphore(10);
	Semaphore oth=new Semaphore(10);
	
    public void run() {
      int times=0;	
      while(times<2){
    	try {      
			oth.acquire();
			//System.out.println("thread");
		} catch (InterruptedException e1) {
		}
    	/*try {
			gate.await();
		} catch (InterruptedException | BrokenBarrierException e) {
		}*/
    	Random rg=new Random();  
    	int num=rg.nextInt(10);
    	int op=rg.nextInt(3);
    	
    	long me=Thread.currentThread().getId();
    	if(op==0)
    	{
    	   System.out.println("Thread ID "+me+" is trying to add "+num);
    	   cl.add(num);                                              //addition
    	}
    	else if(op==1)
    	{  
    		System.out.println("Thread ID "+me+" is trying to remove minimum.");
    		cl.removeMin();
    	}
    	
    //comment here	
    /*	else 
    	{  
    		System.out.println("Thread ID "+me+" is checking containment of "+num);
    		cl.contains(num);
    	}	
      */  
      //  System.out.println("len="+cl.len+"\n");
    	times++;
        mn.release();
        
      }  
    }

    
}
    
