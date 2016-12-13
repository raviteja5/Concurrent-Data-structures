import java.lang.Math;
import java.util.concurrent.atomic.AtomicMarkableReference;
import java.util.concurrent.atomic.AtomicStampedReference;
public class bst {
    bstnode root=new bstnode();
	snode search(int key)
	{
		internal gp = null,p = null;
		AtomicStampedReference<info> gpupdate = null,pupdate = null;
		bstnode l =root;
		while(l.type==1)
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
	{
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
			if(l.key==key)
				return false;
			if(pupdate.getStamp()!=state.clean.getVal())
				help(pupdate);
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
				

				op=new iinfo(p,l,newInternal);
				result=p.update.compareAndSet(pupdate.getReference(),op,pupdate.getStamp(),state.iflag.getVal());
				if (result)
				{ helpinsert(op);
				  return true;
				}
				else
				  help(p.update);
					
			
			}
		}
	}
	
	void helpinsert(info op)
	{
		caschild(op.p,op.l,op.newInternal);
		op.p.update.compareAndSet(op,op,state.iflag.getVal(),state.clean.getVal());
		
	}
	
	boolean delete(int key)
	{
		internal gp = null,p = null;
		leaf l = null;
		AtomicStampedReference<info> pupdate = null,gpupdate = null;
		boolean result;
		info op;
		
		while(true)
		{
			snode temp;
			temp=search(key);
			if(l.key!=key)
				return false;
			if(gpupdate.getStamp()!=state.clean.getVal())
				help(gpupdate);
			else if(pupdate.getStamp()!=state.clean.getVal())
				help(pupdate);
			else
			{
				op=new info(gp,p,l,pupdate);
				result=gp.update.compareAndSet(gpupdate.getReference(),op,gpupdate.getStamp(),state.dflag.getVal());
				if (result)
				{
					if(helpdelete(op))
						return true;
				}
				else
					help(gp.update);
			}
		}
	}
	
	boolean helpdelete(info op)
	{
		boolean result;
		int[] curStamp = null;
		info curInfo;
		result=op.p.update.compareAndSet(op.pupdate.getReference(),op,op.pupdate.getStamp(),state.mark.getVal()); //check
		curInfo=op.p.update.get(curStamp);
		if (result=true || (curInfo==op && state.mark.getVal()==curStamp[0]))
		{
			helpmarked(op);
			return true;
		}
		else
		{
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
