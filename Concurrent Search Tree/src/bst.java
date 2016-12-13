import java.lang.Math;
import java.util.concurrent.atomic.AtomicMarkableReference;
import java.util.concurrent.atomic.AtomicStampedReference;
public class bst {
    internal root;
    bst()
    {  root=new internal(2000);
       bstnode left=new leaf(1000);
       bstnode right=new leaf(2000);
       root.left.set(left);
       root.right.set(right);	
    }
    void printbst(bstnode n)
    {  if(n==null)
       { System.out.println("Tree in depth first:");
         n=root;
       }
       System.out.println(n.key);
       if(n.type==0)
       {   internal temp=(internal)n;
    	   printbst( temp.left.get());
    	   printbst( temp.right.get());
       }
    }
	snode search(int key)
	{
		internal gp = null,p = null;
		AtomicStampedReference<info> gpupdate = null,pupdate = null;
		bstnode l =root;
		while(l.type==0)
		{
			gp=p;
			p=(internal) l;
			gpupdate=pupdate;
			pupdate=p.update;
			if(key<l.key)
				l=p.left.get();
			else
				l=p.right.get();
		}
		return new snode(gp,p,l,pupdate,gpupdate);
		
	}
	
	leaf find(int key)
	{
		leaf l = null;
		snode temp=new snode();
		temp=search(key);
		if(temp.l.key==key)
			return l;
		else
			return null;
	}
	
	boolean insert(int key)
	{   long me=Thread.currentThread().getId();
	    System.out.println("Thread "+me+" entered insert block.");
	    
		internal p = null,newInternal;
		leaf l = null, newSibling;
		leaf newl=new leaf(key);
		AtomicStampedReference<info> pupdate = null;
		boolean result;
		info op;
		
		while(true)
		{   state t;
			snode temp;
			temp=search(key);
			l=(leaf) temp.l;
			p=temp.p;
			pupdate=temp.pupdate;
			if(l.key==key)
		    { System.out.println("Thread "+me+" did not insert "+key+" as it's present already.");
			  return false;
		    }
			if(pupdate.getStamp()!=state.clean.getVal())
			{	System.out.println("Thread "+me+" found parent node unclean before adding "+key);
			    help(pupdate);
			}
			else
			{
				newSibling=new leaf(l.key);
				newInternal=new internal(Math.max(key,l.key));
				newInternal.update.set(null, state.clean.getVal());
				if(newl.key<newSibling.key){
					newInternal.left.set(newl);
					newInternal.right.set(newSibling);
				}
				else{
					newInternal.left.set(newSibling);
					newInternal.right.set(newl);
				}
				

				op=new info(p,l,newInternal);
				result=p.update.compareAndSet(pupdate.getReference(),op,pupdate.getStamp(),state.iflag.getVal());
				if (result)
				{ helpinsert(op);
				  System.out.println("Thread "+me+" successfully inserted "+key);
				  return true;
				}
				else
				{   System.out.println("Thread "+me+" found that flag changed before CAS.");
					help(p.update);
				}
				
			}
		}
	}
	
	void helpinsert(info op)
	{
		caschild(op.p,op.l,op.newInternal);
		op.p.update.compareAndSet(op,op,state.iflag.getVal(),state.clean.getVal());
		
	}
	
	boolean delete(int key)
	{   long me=Thread.currentThread().getId();
		internal gp = null,p = null;
		leaf l = null;
		AtomicStampedReference<info> pupdate = null,gpupdate = null;
		boolean result;
		info op;
		
		while(true)
		{
			snode temp;
			temp=search(key);
			l=(leaf) temp.l;
			p=temp.p;
			gp=temp.gp;
			pupdate=temp.pupdate;
			gpupdate=temp.gpupdate;
			if(l.key!=key)
			{	System.out.println("Thread "+me+" could'nt delete "+key);
				return false;
			}
			if(gpupdate.getStamp()!=state.clean.getVal())
			{  System.out.println("Thread "+me+" found grand parent unclean during deletion of "+key);
			   help(gpupdate);
			}
			else if(pupdate.getStamp()!=state.clean.getVal())
			{  System.out.println("Thread "+me+" found parent unclean during deletion of "+key);
			   help(pupdate);
			}
			else
			{
				op=new info(gp,p,l,pupdate);
				result=gp.update.compareAndSet(gpupdate.getReference(),op,gpupdate.getStamp(),state.dflag.getVal());
				if (result)
				{
					if(helpdelete(op))
					{	System.out.println("Thread "+me+" succesfully deleted "+key);
						return true;
					}
				}
				else
				{   System.out.println("Thread "+me+" found GP became unclean before CAS during deletion of "+key);
				    help(gp.update);
				}
			}
		}
	}
	
	boolean helpdelete(info op)
	{   long me=Thread.currentThread().getId();
		boolean result;
		//changing here
		int[] curStamp =new int[5] ;
		info curInfo;
		result=op.p.update.compareAndSet(op.pupdate.getReference(),op,op.pupdate.getStamp(),state.mark.getVal()); //check
		curInfo=op.p.update.get(curStamp);
		if (result==true || (curInfo==op && state.mark.getVal()==curStamp[0]))
		{
			helpmarked(op);
			return true;
		}
		else
		{   System.out.println("Thread "+me+" found parent became unclean before CAS during deletion of key");
			help(op.p.update);
			op.gp.update.compareAndSet(op,op,state.dflag.getVal(),state.clean.getVal());
			return false;
		}
	}
	
	void helpmarked(info op)
	{  
		bstnode other;
		if(op.p.right.get()==op.l)
			other=op.p.left.get();
		else
			other=op.p.right.get();
		caschild(op.gp,op.p,other);
		op.gp.update.compareAndSet(op,op,state.dflag.getVal(),state.clean.getVal());
		
	}
	
	void help(AtomicStampedReference<info> u)
	{
		if(u.getStamp() == state.iflag.getVal())
			helpinsert(u.getReference());
		else if(u.getStamp()==state.mark.getVal())
			helpmarked(u.getReference());
		else if(u.getStamp()==state.dflag.getVal())
			helpdelete(u.getReference());
		
	}
	
	void caschild(internal parent,bstnode old, bstnode newc)
	{
		if(newc.key < parent.key)
			parent.left.compareAndSet(old,newc);
		else
			parent.right.compareAndSet(old,newc);
	}
}
