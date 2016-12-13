import java.util.concurrent.locks.*;

public class CoarseList {
  public Node head;
  private Lock lock = new ReentrantLock();
  public int len;
  public CoarseList(int n) {
   head = new Node(Integer.MIN_VALUE);
   head.next = new Node(Integer.MAX_VALUE);
   len=n;
  }
  public boolean add(int key)  {
  long me=Thread.currentThread().getId();
  Node pred, curr;
  lock.lock();
  System.out.println("Started by :"+ me+"\n");
  
  try {
   pred = head;
   curr = pred.next;
   while (curr.key < key) {
    pred = curr;
    curr = curr.next;
   }
   if (key == curr.key) {
       System.out.println("Number not added!");
	   return false;
   } 
   else {
    Node node = new Node(key);
    node.next = curr;
    pred.next = node;
    this.len++;
    return true;
   }
  } 
  finally {
	  System.out.println("Ended by :"+ me+"\n");
	  lock.unlock();
   
   
  }
  
 }
}