import java.util.concurrent.atomic.*;
public class internal extends bstnode {
   AtomicStampedReference<info> update;
   AtomicReference<bstnode> left,right;
   internal(int key)
   {
	   this.type=0;
	   this.key=key;
	   left=new AtomicReference<bstnode>();
	   right=new AtomicReference<bstnode>();
	   update=new AtomicStampedReference<info>(null, 1);
   }

}
