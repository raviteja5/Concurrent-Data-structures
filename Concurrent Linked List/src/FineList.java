 

public class FineList {
	public Node head;
	public int len;
	public FineList(int n) {
	  head = new Node(Integer.MIN_VALUE);
	  head.next = new Node(Integer.MAX_VALUE);
	  len=n;
	}
	public boolean add(int key) {
		 long me=Thread.currentThread().getId();
		 head.lock.lock();
		 System.out.println("Thread "+me+" has locked head for addition of "+key);
		 Node pred = head;
		 try {
		  Node curr = pred.next;
		  curr.lock.lock();
		  System.out.println("Thread "+me+" has locked "+curr.key);
		  try {
		   while (curr.key < key) {
		    pred.lock.unlock();
		    System.out.println("Thread "+me+" unlocked "+pred.key);
		    pred = curr;
		    curr = curr.next;
		    curr.lock.lock();
		    System.out.println("Thread "+me+" has locked "+curr.key);
		   }
		   System.out.println("Started by :"+ me);
		   if (curr.key == key) {
			   System.out.println("Number not added!");
		       return false;
		   }
		   Node newNode = new Node(key);
		   newNode.next = curr;
		   pred.next = newNode;
		   this.len++;
		   System.out.println("Thread "+me+" added "+key);
		   return true;
		  } finally {
		     curr.lock.unlock();
		    }
		 } finally {
			System.out.println("Ended by :"+ me);
		    pred.lock.unlock();
		   }
   }
   public boolean remove(int key) {

	 	 Node pred = null, curr = null;
	 	 long me=Thread.currentThread().getId();
		 head.lock.lock();
		 System.out.println("Thread "+me+" has locked head for removal of "+key);
		 try {
		  pred = head;
		  curr = pred.next;
		  curr.lock.lock();
		  System.out.println("Thread "+me+" has locked "+curr.key);
		  try {
		   while (curr.key < key) {
		    pred.lock.unlock();
		    System.out.println("Thread "+me+" has unlocked "+pred.key);
		    pred = curr;
		    curr = curr.next;
		    curr.lock.lock();
		    System.out.println("Thread "+me+" has locked "+curr.key);
		   }
		  System.out.println("Started by :"+ me);
		  if (curr.key == key) {
		   pred.next = curr.next;
		   len-=1;
		   System.out.println("Thread "+me+" removed "+key);
		   return true;
		  }
		  System.out.println("Thread "+me+" did not find "+key);
		  return false;
		  } finally {
		     curr.lock.unlock();
		    }
		 } finally {
			 System.out.println("Ended by :"+ me);
			 pred.lock.unlock();
		   }
  }
   public boolean contains(int key){return true;};
} 
