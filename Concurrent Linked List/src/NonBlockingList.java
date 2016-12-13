
import java.util.concurrent.atomic.AtomicMarkableReference;
public class NonBlockingList {

	
	public Node head,tail;
	public int len;
	
	public NonBlockingList(int n) {
	  head = new Node(Integer.MIN_VALUE);
	  tail = new Node(Integer.MAX_VALUE);
	  head.Next = new AtomicMarkableReference<Node>(tail, false);
	  tail.Next = new AtomicMarkableReference<Node>(null, false);
	  len=n;
	}
	
	
	public Window find(Node head, int key) {
		 Node pred = null, curr = null, succ = null;
		 long me=Thread.currentThread().getId();
		 boolean[] marked = {false};
		 boolean snip;
		 retry: while (true) {
		  pred = head;
		  curr = pred.Next.getReference();
		  while (true) {
		   succ = curr.Next.get(marked);
		   while (marked[0]) {
		    snip = pred.Next.compareAndSet(curr, succ, false, false);
		    if (!snip) continue retry;
		    System.out.println("Thread "+me+" physically removed "+curr.key+" during find.");
		    curr = succ;
		    succ = curr.Next.get(marked);
		   }
		   if (curr.key >= key)
		    return new Window(pred, curr);
		   pred = curr;
		   curr = succ;
		  }
		 }
	}
	
	public boolean add(int key) {
		 long me=Thread.currentThread().getId();
		 while (true) {
		  Window window = find(head, key);
		  Node pred = window.pred, curr = window.curr;
		  if (curr.key == key) {
		   System.out.println("Thread "+me+" did not add "+key);
		   return false;
		  } else {
		     Node node = new Node(key);
		     node.Next = new AtomicMarkableReference<Node>(curr, false);
		     if (pred.Next.compareAndSet(curr, node, false, false)) {
		    	System.out.println("Thread "+me+" added "+key); 
		    	len++;
		    	return true;
		     }
		    }
		}
	}
	
	public boolean remove(int key) {
		 long me=Thread.currentThread().getId();
		 boolean snip;
		 while (true) {
		  Window window = find(head, key);
		  Node pred = window.pred, curr = window.curr;
		  if (curr.key != key) {
		   System.out.println("Thread "+me+" found no "+key+" to remove.");
		   return false;
		  } else {
		     Node succ = curr.Next.getReference();
		     snip = curr.Next.attemptMark(succ, true);
		     if (!snip)
		    	 continue;
		     else
		     { System.out.println("Thread "+me+" logically removed "+key);
		       len--;
		     }
		     if(!pred.Next.compareAndSet(curr, succ, false, false)){
		    	 System.out.println("Thread "+me+" could not physically remove "+key);
		     }
		     return true;
		  }
		 }
	}
		
}
