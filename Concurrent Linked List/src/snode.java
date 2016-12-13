import java.util.concurrent.atomic.AtomicStampedReference;


public class snode {
	internal gp,p;
	AtomicStampedReference<info> gpupdate,pupdate;
	bstnode l;
	snode(internal gp,internal p,bstnode l,AtomicStampedReference<info> pupdate,AtomicStampedReference<info> gpupdate)
	{ this.gp=gp;
	  this.p=p;
	  this.l=l;
	  this.pupdate=pupdate;
	  this.gpupdate=gpupdate;
		
	}
	public snode() {
		
	}
}
