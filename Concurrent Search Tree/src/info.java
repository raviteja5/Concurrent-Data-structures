import java.util.concurrent.atomic.AtomicStampedReference;


public class info {
   public info(internal gp, internal p, leaf l,	AtomicStampedReference<info> pupdate) {
         this.gp=gp;
         this.p=p;
         this.l=l;
         this.pupdate=pupdate;
	}
public info(internal p, leaf l, internal newInternal) {
    this.p=p;
    this.l=l;
    this.newInternal=newInternal;

}
internal p;
   leaf l;
   internal gp;
   AtomicStampedReference<info> pupdate;
   internal newInternal;
}
