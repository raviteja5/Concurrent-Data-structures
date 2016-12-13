import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.atomic.AtomicMarkableReference;
public class Node {
	int key;
	Node next;
	boolean marked;
	public Lock lock = new ReentrantLock();
	AtomicMarkableReference<Node> Next;
	Node (int n)
	{  this.key=n;
	}
	
}