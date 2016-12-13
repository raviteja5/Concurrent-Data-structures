import java.util.concurrent.atomic.AtomicInteger;

public class OptimisticList {
	public Node head; 
	public int len;
	int[][] lin_order=new int[100][4];      // id, method, element, result
	AtomicInteger ai=new AtomicInteger();
	
	public OptimisticList(int n) {
	  head = new Node(Integer.MIN_VALUE);
	  head.next = new Node(Integer.MAX_VALUE);
	  len=n;
	}
	public boolean add(int key) {
		 long me= Thread.currentThread().getId();
		 while (true) {
		  Node pred = head;
		  System.out.println("Thread "+me+" traversed head");
		  Node curr = pred.next;
		  System.out.println("Thread "+me+" traversed "+curr.key);
		  while (curr.key < key) {
		   pred = curr; curr = curr.next;
		   System.out.println("Thread "+me+" traversed "+curr.key);
		  }
		  pred.lock.lock(); curr.lock.lock();
		  System.out.println("Thread "+me+" locked "+pred.key+" and "+curr.key);
		  try {
		   if (validate(pred, curr)) {			
			int my_order;
			my_order=ai.getAndIncrement();
			lin_order[my_order][0]=(int) me;
			lin_order[my_order][1]=0;
			lin_order[my_order][2]=key;
		    if (curr.key == key) {
		      lin_order[my_order][3]=1;
		      System.out.println("Number not inserted");
		      return false;
		    } else {
		     lin_order[my_order][3]=0;
		     Node node = new Node(key);
		     node.next = curr;
		     pred.next = node;
		     len++;
			 System.out.println("Thread "+me+" added the number "+key);
		     return true;
		    }
		   }else
			  System.out.println("Validation failed! Hahaha");
		  } finally {
		  pred.lock.unlock(); curr.lock.unlock();
		  System.out.println("Thread "+me+" unlocked "+pred.key+" and "+curr.key);
		 }
		}
	}
	
	public boolean remove(int key) {
		 long me=Thread.currentThread().getId();
		 while (true) {
		  Node pred = head;
		  System.out.println("Thread "+me+" traversed head");
		  Node curr = pred.next;
		  System.out.println("Thread "+me+" traversed "+curr.key);
		  while (curr.key < key) {
		   pred = curr; curr = curr.next;
		   System.out.println("Thread "+me+" traversed "+curr.key);
		  }
		  pred.lock.lock(); curr.lock.lock();
		  System.out.println("Thread "+me+" locked "+pred.key+" and "+curr.key);
		  try {
		   if (validate(pred, curr)) {
			int my_order;
			my_order=ai.getAndIncrement();
			lin_order[my_order][0]=(int) me;
			lin_order[my_order][1]=1;
			lin_order[my_order][2]=key;
		    if (curr.key == key) {
		      lin_order[my_order][3]=0;
		      pred.next = curr.next;
		      len--;
		      System.out.println("Thread "+me+" removed the number "+key);		      
		      return true;
		    } else {
		    	   lin_order[my_order][3]=1;
		           System.out.println("Thread "+me+" did not find the number "+key+" for removal.");
		           return false;
		    }		    
		   }
		   else{
			   System.out.println("Validation failed! Hahaha");
		   }		   
		  } finally {
		   pred.lock.unlock(); curr.lock.unlock();
		   System.out.println("Thread "+me+" unlocked "+pred.key+" and "+curr.key);
		  }
		 }
    }
	
	public boolean contains(int key) {
		 long me=Thread.currentThread().getId(); 
		 while (true) {
		  Node pred = this.head;
		  System.out.println("Thread "+me+" traversed head");
		  Node curr = pred.next;
		  System.out.println("Thread "+me+" traversed "+curr.key);
		  while (curr.key < key) {
		   pred = curr; curr = curr.next;
		   System.out.println("Thread "+me+" traversed "+curr.key);
		  }
		  try {
		   pred.lock.lock(); curr.lock.lock();
		   System.out.println("Thread "+me+" locked "+pred.key+" and "+curr.key);
		   if (validate(pred, curr)) {
			 int my_order;
			 my_order=ai.getAndIncrement();
			 lin_order[my_order][0]=(int) me;
			 lin_order[my_order][1]=2;
			 lin_order[my_order][2]=key;
			 if(curr.key == key)
			 {	 System.out.println("Thread "+me+" found "+key+" in the list.");
			     lin_order[my_order][3]=0;
			 }
			 else
			 {	 System.out.println("Thread "+me+" did not find "+key+" in the list.");
			     lin_order[my_order][3]=1;
			 }
		     return (curr.key == key);
		   }
		   else
			   System.out.println("Validation failed! Hahaha");
		  } finally { 
		   pred.lock.unlock(); curr.lock.unlock();
		  }
		}
	}
	private boolean validate(Node pred, Node curr) {
		 Node node = head;
		 while (node.key <= pred.key) {
		  if (node == pred)
		   return pred.next == curr;
		  node = node.next;
		 }
		 return false;
	}
}
