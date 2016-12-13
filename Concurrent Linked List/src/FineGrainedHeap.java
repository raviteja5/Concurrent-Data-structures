import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FineGrainedHeap {
 private static int ROOT = 1;
 private static int NO_ONE = -1;
 private Lock heapLock;
 int next;
  HeapNode[] heap;
 public FineGrainedHeap(int capacity) {
  heapLock = new ReentrantLock();
  next = ROOT;
  heap = (HeapNode[]) new HeapNode[capacity + 1];
  for (int i = 0; i < capacity + 1; i++) {
   heap[i] = new HeapNode();
  }
 }
 
 private void swap(int a,int b){
	 HeapNode temp=new HeapNode();
	 
	 temp.item=heap[b].item;
	 temp.score=heap[b].score;
	 temp.tag=heap[b].tag;
	 temp.owner=heap[b].owner;
	 
	 heap[b].item=heap[a].item;
	 heap[b].score=heap[a].score;
	 heap[b].tag=heap[a].tag;
	 heap[b].owner=heap[a].owner;
	 
	 heap[a].item=temp.item;
	 heap[a].score=temp.score;
	 heap[a].tag=temp.tag;
	 heap[a].owner=temp.owner;
	 
	 
	 
 }
 private static enum Status {EMPTY, AVAILABLE, BUSY};
 private static class HeapNode {
  Status tag;
  int score;
  int item;
  long owner;
  Lock lock;
  public void init( int myScore) {
   
   score = myScore;
   tag = Status.BUSY;
   owner = Thread.currentThread().getId();
  }
  public HeapNode() {
   tag = Status.EMPTY;
   lock = new ReentrantLock();
  }
  public void lock() {lock.lock();}
  public void unlock() {lock.unlock();}
  public boolean amOwner() {
	long me=Thread.currentThread().getId();
	return tag==Status.BUSY && owner==me;
  }
 }
 public void add(int score) {
	  long me=Thread.currentThread().getId();
	  heapLock.lock();
	  System.out.println("Thread "+me+" obatined lock for heap.");
	  int child = next++;
	  heap[child].lock();                 
	  heap[child].init(score);
	  System.out.println("Thread "+me+" initialized node "+child+" with "+score);
	  heapLock.unlock();
	  heap[child].unlock();
	 
	  while (child > ROOT) {
	   int parent = child / 2;
	   System.out.println("add bef locks");
	   heap[parent].lock();
	   heap[child].lock();
	   System.out.println("add after locks");
	   int oldChild = child;
	   try {
	    if (heap[parent].tag == Status.AVAILABLE && heap[child].amOwner()) {
	     if (heap[child].score < heap[parent].score) {
	      swap(child, parent);
	      System.out.println("Thread "+me+" swapped nodes "+child+" and "+parent);
	      child = parent;
	     } else {
	      heap[child].tag = Status.AVAILABLE;
	      heap[child].owner = NO_ONE;
	      System.out.println("Thread "+me+" added "+score);
	      System.out.println("Finished "+me);
	      return;
	     }
	    } else if (!heap[child].amOwner()) {
	     System.out.println("Thread "+me+" moved up hoping to find "+score);
	     child = parent;
	    }
	   } finally {
	    heap[oldChild].unlock();
	    heap[parent].unlock();
	   }
	 }
	 if (child == ROOT) {
	  heap[ROOT].lock();
	  System.out.println("Thread "+me+" moved up till root.");
	  if (heap[ROOT].amOwner()) {
	   heap[ROOT].tag = Status.AVAILABLE;
	   heap[child].owner = NO_ONE;
	   System.out.println("Thread "+me+" added "+score);
	  }
	  heap[ROOT].unlock();
	 }
	 System.out.println("Finished "+me);
  }
 
 
 
 
  public int removeMin() {
	  long me=Thread.currentThread().getId();
	  System.out.println("remove by "+me);
	  heapLock.lock();
	  System.out.println("lalala");
	  
	  if(next==1){
		  System.out.println("Thread "+me+" found no element to remove");
		  heapLock.unlock();
		  System.out.println("Finished "+me);
		  return 0;
	  }
	  int bottom = --next;
	  System.out.println("rem1 bef locks");
	  heap[bottom].lock();
	  heap[ROOT].lock();
	  System.out.println("rem2 bef locks");
	  heapLock.unlock();
	  int item = heap[ROOT].item;
	  heap[ROOT].tag = Status.EMPTY;
	  heap[ROOT].owner = NO_ONE;
	  swap(bottom, ROOT);
	  System.out.println("Thread "+me+" swapped noded "+bottom+" and "+ROOT);
	  heap[bottom].unlock();
	  if (heap[ROOT].tag == Status.EMPTY) {
	   System.out.println("Thread "+me+" removed the root and left an empty heap.");	  
	   heap[ROOT].unlock();
	   System.out.println("Finished "+me);
	   return item;
	  }
	  int child = 0;
	  int parent = ROOT;
	  while (parent < heap.length / 2) {
	   System.out.println("while1 "+me);
	   int left = parent * 2;
	   int right = (parent * 2) + 1;
	   System.out.println("rem2 bef locks");
	   
	   heap[left].lock();
	   
	   heap[right].lock();
	   System.out.println("rem2 after locks");
	   if (heap[left].tag == Status.EMPTY) {
	     heap[right].unlock();
	     heap[left].unlock();
	     System.out.println("Thread "+me+" returned since leaf node is reached.");
	     break;
	   } else if (heap[right].tag == Status.EMPTY || heap[left].score < heap[right].score) {
	    heap[right].unlock();
	    child = left;
	   } else {
	    heap[left].unlock();
	    child = right;
	   }
	   if (heap[child].score < heap[parent].score) {
	    swap(parent, child);
	    System.out.println("Thread "+me+" swapped nodes "+parent+" with "+child);
	    heap[parent].unlock();
	    parent = child;
	   } else {
	    heap[child].unlock();
	    System.out.println("Thread "+me+" returned since reached required position.");
	    break;
	   }
	  }
	  heap[parent].unlock();
	  System.out.println("Finished "+me);
	  return item;
	}
    public void print(){
    	
    	int len=next-1,i=1;
    	System.out.println("State of Heap:");
    	while(i<=len){
    		System.out.print(heap[i].score+" ");
    		i++;
    	}
    	System.out.println();
    		
    }	
   	 
}