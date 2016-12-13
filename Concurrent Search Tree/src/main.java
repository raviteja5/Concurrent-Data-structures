
public class main {

	
   public static void main(String[] args) {
        int threads=10;
    	CreateThreadRunnableExample p =new CreateThreadRunnableExample();
    	p.cl.printbst(null);
    	for(int i=0;i<5;i++)
			try {
				p.mn.acquire();
				
			} catch (InterruptedException e) {				
			}
    	for(int i=0;i<threads ;i++)                     
        {  Thread t = new Thread(p);
           t.start();    
        }
    	
        
      //printing of data structure occasionally
       int times=2;
       int ptimes=4;
       int n_sem=(threads*times)/ptimes;
       
      while(ptimes>0){
      	
    	  for(int i=0;i<n_sem;i++)
	        try {
              p.mn.acquire();
              
	  		} catch (InterruptedException e) {				
	  	  }    	          
	  		
	  		//print what you want to print occasionally
	  	  p.cl.printbst(null);
          for(int i=0;i<n_sem;i++)
  	   		p.oth.release();
  	   	  ptimes--;	    	  
      }
    	
      System.out.println("Main finished!");
   }    
}
