import java.util.concurrent.Semaphore;
import java.util.*;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;


public class CreateThreadRunnableExample implements Runnable {
	bst cl=new bst();                    //method to implement
	//final CyclicBarrier gate = new CyclicBarrier(10);
	int ptimes=4;
	int threads=10;
	int times=2;
	int n_sem=(threads*times)/ptimes;
	Semaphore mn=new Semaphore(n_sem);      
	Semaphore oth=new Semaphore(n_sem);
	
    public void run() {
      long me=Thread.currentThread().getId();
      int ttimes=times;  //store value for each thread	
      while(ttimes>0){
    	try {      
			oth.acquire();
		
		} catch (InterruptedException e1) {
		}
    	Random rg=new Random();  
    	int num=rg.nextInt(10);
    	int op=rg.nextInt(3);
    	
    	
    	if(op==0)
    	{
    	   System.out.println("Thread ID "+me+" is trying to add "+num);
    	   cl.insert(num);                                              //addition
    	}
    	else if(op==1)
    	{  
    		System.out.println("Thread ID "+me+" is trying to delete "+num);
    		cl.delete(num);
    	}
    	ttimes--;    	
        mn.release();
        
      }  
      
    }

    
}
    
