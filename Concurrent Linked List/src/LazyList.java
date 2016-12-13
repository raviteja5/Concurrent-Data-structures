
public class LazyList {
	
	public Node head;
	public int len;
	public LazyList(int n) {
	  head = new Node(Integer.MIN_VALUE);
	  head.next = new Node(Integer.MAX_VALUE);
	  len=n;
	}
	
	
	
	
	private boolean validate(Node pred, Node curr) {
		 return !pred.marked && !curr.marked && pred.next == curr;
	}
	
	public boolean add(int key) {
		 long me=Thread.currentThread().getId();
		 while (true) {
		  Node pred = head;
		  System.out.println("Thread "+me+" traversed head");
		  Node curr = head.next;
		  if(curr.marked==true){
			   System.out.println("Thread "+me+" traversed \"marked "+curr.key+"\"");
		   }
		  else
			  System.out.println("Thread "+me+" traversed "+curr.key);
		  while (curr.key < key) {
		   pred = curr; curr = curr.next;
		   if(curr.marked==true){
			   System.out.println("Thread "+me+" traversed \"marked "+curr.key+"\"");
		   }
		   else
			   System.out.println("Thread "+me+" traversed "+curr.key);
		  }
		  pred.lock.lock();
		  try {
		   curr.lock.lock();
		   System.out.println("Thread "+me+" locked "+pred.key+" and "+curr.key);
		   try {
		    if (validate(pred, curr)) {
		     if (curr.key == key) {
		      System.out.println("Number not inserted");
		      return false;
		     } else {
		         Node node = new Node(key);
		         node.next = curr;
		         pred.next = node;
		         node.marked=false;
		         len++;
				 System.out.println("Thread "+me+" added the number "+key);
		         return true;
		     }
		    }else
				System.out.println("Validation failed! Hahaha");
		   } finally {
		   curr.lock.unlock();
		   System.out.println("Thread "+me+" unlocked "+curr.key);
		   }
		 } finally {
		     pred.lock.unlock();
		     System.out.println("Thread "+me+" unlocked "+pred.key);
		 }
		}
   }
	
   
	public boolean remove(int key)  {
		 long me=Thread.currentThread().getId();
		 while (true) {
		  Node pred = head;
		  System.out.println("Thread "+me+" traversed head");
		  Node curr = head.next;
		  if(curr.marked==true){
			   System.out.println("Thread "+me+" traversed \"marked "+curr.key+"\"");
		   }
		  else
			  System.out.println("Thread "+me+" traversed "+curr.key);
		  while (curr.key < key) {
		   pred = curr; curr = curr.next;		   
		   if(curr.marked==true){
			   System.out.println("Thread "+me+" traversed \"marked "+curr.key+"\"");
		   }
		   else
			   System.out.println("Thread "+me+" traversed "+curr.key);
		  }
		  pred.lock.lock();
		  try {
		   curr.lock.lock();
		   System.out.println("Thread "+me+" locked "+pred.key+" and "+curr.key);
		   try {
		    if (validate(pred, curr)) {
		     if (curr.key != key) {
		      System.out.println("Thread "+me+" did not find the number "+key+" for removal.");
		      return false;
		     } else {
		        curr.marked = true;
		        System.out.println("Thread "+me+" logically removed the number "+key);
		        Thread.sleep(100);
		        pred.next = curr.next;
		        System.out.println("Thread "+me+" physically removed the number "+key);
		        len--;		        
		        return true;
		     }
		    }else
		    	System.out.println("Validation failed! Hahaha");
		   } catch (InterruptedException e) {
			
		} finally {
		       curr.lock.unlock();
		       System.out.println("Thread "+me+" unlocked "+curr.key);
		   }
		 } finally {
		     pred.lock.unlock();
		     System.out.println("Thread "+me+" unlocked "+pred.key);
		 }
	    }
   }
	
	
	public boolean contains(int key) {		 
		 Node curr = head;
		 while (curr.key < key)
		  curr = curr.next;
		 return curr.key == key && !curr.marked;
	}
	
	
	
}
