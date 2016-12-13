import java.util.concurrent.atomic.AtomicStampedReference;


public class info {
   public info(internal gp, internal p, leaf l,	AtomicStampedReference<info> pupdate) {
         this.gp=gp;
         this.p=p;
         this.l=l;
         this.pupdate=pupdate;
	}
internal p;
   leaf l;
   internal gp;
   AtomicStampedReference<info> pupdate;
   internal newInternal;
}
