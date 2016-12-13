
public class main {

	
   public static void main(String[] args) {
        int threads=50;
    	CreateThreadRunnableExample p =new CreateThreadRunnableExample();
        for(int i=0;i<threads ;i++)                     
        {  Thread t = new Thread(p);
           t.start();    
        }
        try {
			Thread.sleep(500);
		} catch (InterruptedException e) {}
      
      //comment here
        /*  
        //printing list
        System.out.print("\nFinal Linked List:\nHead ");
        Node temp=p.cl.head.next;
        while(temp.key!=Integer.MAX_VALUE){
        	System.out.print(temp.key+" ");
            temp=temp.next;
        }
        System.out.println("Tail");
        
           
          // printing linearization
            System.out.println("\nLinearized to:");
            for(int i=0;i<threads ;i++)
            {	System.out.print("Thread "+p.cl.lin_order[i][0]);
                switch (p.cl.lin_order[i][1])
                {  case 0: System.out.print(" adding ");
                           break;
                   case 1: System.out.print(" removing ");
                           break;
                   case 2: System.out.print(" contains ");
                           break;
                }
                System.out.print(p.cl.lin_order[i][2]+" with result ");
                if(p.cl.lin_order[i][3]==1)
                   System.out.println("fail.");                         
                else
                   System.out.println("success.");
            }
        	
        */
        //till here
        
        
   }
    
}
