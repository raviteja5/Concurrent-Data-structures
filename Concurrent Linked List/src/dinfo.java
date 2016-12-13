import java.util.concurrent.atomic.AtomicStampedReference;


public class dinfo extends info{

	public dinfo(internal gp, internal p, leaf l, AtomicStampedReference<bstnode> pupdate) {
		this.gp=gp;
		this.p=p;
		this.pupdate=pupdate;
		this.l=l;
	}
	internal gp;
	AtomicStampedReference<bstnode> pupdate;
	
}
