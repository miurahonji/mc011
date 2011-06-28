package tree;

import assem.Instr;
import assem.Targets;
import util.List;
import temp.Temp;
import temp.Label;
import x86.Frame;

public class MaxMunch
{
	public List<Rest> info = null;
	public List<Rest> last = null;

    public MaxMunch(Stm s) {
        if ( s == null )
            return;
        
        if ( s instanceof CJUMP )
            maxMunch( (CJUMP) s);
        else if ( s instanceof EXPSTM )
            maxMunch( (EXPSTM) s);
        else if ( s instanceof JUMP )
            maxMunch( (JUMP) s);
        else if ( s instanceof LABEL )
            maxMunch( (LABEL) s);
        else if ( s instanceof MOVE )
            maxMunch( (MOVE) s);
        else if ( s instanceof SEQ )
            maxMunch( (SEQ) s);
        else
            throw new Error("Unexpected: " + s.getClass());
	}

	private Rest maxMunch(Stm s)
	{
		return new Rest();
	}

	private Rest maxMunch(CJUMP s)
	{
		Rest r = new Rest();
		Rest left = maxMunch(s.left);
		Rest right = maxMunch(s.right);
		r.addSrc(right.dst.head);
		r.addDst(left.dst.head);
		defineRest("cmp `d0, `s0", r, Rest.OPER);
		switch (s.op)
		{
			case CJUMP.EQ:
				defineRest("je " + s.ifTrue, r, Rest.OPER);
				break;
			case CJUMP.NE:
				defineRest("jne " + s.ifTrue, r, Rest.OPER);
				break;
			case CJUMP.LT:
				defineRest("jl " + s.ifTrue, r, Rest.OPER);
				break;
			case CJUMP.LE:
				defineRest("jle " + s.ifTrue, r, Rest.OPER);
				break;
			case CJUMP.GT:
				defineRest("jg " + s.ifTrue, r, Rest.OPER);
				break;
			case CJUMP.GE:
				defineRest("jge " + s.ifTrue, r, Rest.OPER);
				break;
			case CJUMP.ULT:
				defineRest("jl " + s.ifTrue, r, Rest.OPER);
				break;
			case CJUMP.ULE:
				defineRest("jle " + s.ifTrue, r, Rest.OPER);
				break;
			case CJUMP.UGT:
				defineRest("jg " + s.ifTrue, r, Rest.OPER);
				break;
			case CJUMP.UGE:
				defineRest("jge " + s.ifTrue, r, Rest.OPER);
				break;
			default:
				System.out.println("Wrong OP on CJUMP");
				break;
		}
		return r;
	}

	private Rest maxMunch(EXPSTM s)
	{
		return maxMunch(s.exp);
	}

	private Rest maxMunch(JUMP s)
	{
		Rest r = new Rest();
		//for (List<Label> x = s.targets; x != null; x = x.tail)
		//	r.addTarget(x.head);
		r.targets = s.targets;
		defineRest("jmp `j0", r, Rest.OPER_TARGET);
		return r;
	}

	private Rest maxMunch(LABEL s){
		Rest r = new Rest();
		r.label = s.label;
		System.out.println(" Label : " + s.getClass() + s.label);
		defineRest(s.label + ":", r, Rest.LABEL);
		return r;
	}

	private Rest maxMunch(SEQ s)
	{
		maxMunch(s.left);
		maxMunch(s.right);
		return new Rest();
	}

    private Rest maxMunch(MOVE s) {
		Rest r = new Rest();
		if (s.dst instanceof TEMP)
		{
			TEMP t = (TEMP)(s.dst);
			r.addDst(t.temp);
		}
		else
		{
			Rest rr = maxMunch(s.dst);
			r.addDst(rr.dst.head);
		}

		if (s.src instanceof TEMP)
		{
			TEMP t = (TEMP)(s.src);
			r.addSrc(t.temp);
		}
		else
		{
			Rest rr = maxMunch(s.src);
			r.addSrc(rr.dst.head);
		}

		defineRest("mov `d0, `s0", r, Rest.OPER);
		return r;
	}


	private Rest maxMunch(Exp e){		
		System.out.println(" Exp: " + e.getClass());
		Rest r = null;
        if ( e == null )
            return r;
        
        if ( e instanceof BINOP )
            r = maxMunch( (BINOP) e);
        else if ( e instanceof CALL ){
            r = maxMunch( (CALL) e);
		}
        else if ( e instanceof CONST )
            r = maxMunch( (CONST) e);
        else if ( e instanceof ESEQ )
            r = maxMunch( (ESEQ) e);
        else if ( e instanceof MEM )
            r = maxMunch( (MEM) e);
        else if ( e instanceof NAME )
            r = maxMunch( (NAME) e);
        else if ( e instanceof TEMP )
            r = maxMunch( (TEMP) e);
        else
            throw new Error("Unexpected: " + e.getClass());
		return r;
	}

	private Rest maxMunch(BINOP e){		
		System.out.println(" uppercase: " + e.getClass());
		Rest r = new Rest();
		r.addSrc(((Rest)maxMunch(e.right)).dst.head);
		r.addDst(((Rest)maxMunch(e.left)).dst.head);
		switch (e.binop)
		{
			case BINOP.PLUS:
				defineRest("add `d0, `s0", r, Rest.OPER);
				break;
			case BINOP.MINUS:
				defineRest("sub `d0, `s0", r, Rest.OPER);
				break;
			case BINOP.TIMES:
				defineRest("imul `d0, `s0", r, Rest.OPER);
				break;
			case BINOP.DIV:
				defineRest("idiv `d0, `s0", r, Rest.OPER);
				break;
			case BINOP.AND:
				defineRest("and `d0, `s0", r, Rest.OPER);
				break;
			case BINOP.OR:
				defineRest("or `d0, `s0", r, Rest.OPER);
				break;
			case BINOP.LSHIFT:
				r.src = null;
				defineRest("shl `d0, " + ((CONST)e.right).value, r, Rest.OPER);
				break;
			case BINOP.RSHIFT:
				r.src = null;
				defineRest("shr `d0, " + ((CONST)e.right).value, r, Rest.OPER);
				break;
			case BINOP.ARSHIFT:
				r.src = null;
				defineRest("arsh `d0, " + ((CONST)e.right).value, r, Rest.OPER);
				break;
			case BINOP.XOR:
				defineRest("xor `d0, `s0", r, Rest.OPER);
				break;
			default:
				System.out.println("Wrong binop");
		}
    
		return r;
	}


	private Rest maxMunch(CALL e){		
		List<Exp> rev_arg = null;
		for (List<Exp> arg = e.args; arg != null; arg = arg.tail)
		{
			if (rev_arg == null)
				rev_arg = new List<Exp>(arg.head, null);
			else 
				rev_arg = new List<Exp>(arg.head, rev_arg);
		}

		
		for (List<Exp> arg = rev_arg; arg != null; arg = arg.tail)
		{
			Rest rr = maxMunch(arg.head);
			Rest r = new Rest();
			r.addSrc(new Temp());
			// label : move it to a register as string
			if (rr.label != null)
			{
				Temp t = new Temp();
				r.addDst(t);
				defineRest("mov `d0, " + rr.label.toString(), r, Rest.OPER);
				defineRest("push `d0", r, Rest.OPER);
			}
			else
			{
				r.addSrc(new Temp());
				r.addDst(rr.dst.head);
				defineRest("push `d0", r, Rest.OPER);
			}
		}

		Rest rr = maxMunch(e.func);
		if (rr.label != null)
			defineRest("call " + rr.label, rr, Rest.OPER);
		else
			defineRest("call `d0", rr, Rest.OPER);

		if (e.args.size() > 0)
		{
			rr = new Rest();
			rr.addDst(x86.Frame.esp);
			defineRest("add `d0, " + (4*e.args.size()), rr, Rest.OPER);
		}

		rr = new Rest();
		rr.addDst(new Temp());
		rr.addSrc(x86.Frame.eax);
		defineRest("mov `d0, `s0", rr, Rest.OPER);
		return rr;
	}

	private Rest maxMunch(CONST e){		
		System.out.println(" uppercase: " + e.getClass());
		
		Rest r = new Rest();
		//r.addDst(new Temp());
		//defineRest("mov `d0, " + e.value, r, Rest.OPER);
		Temp t = new Temp();
		r.addDst(t);
		defineRest("xor `d0, `d0", r, Rest.OPER);

		r = new Rest();
		r.addDst(t);
		defineRest("add `d0, " + e.value, r, Rest.OPER);

		r = new Rest();
		r.addDst(t);
		return r;
	}

	private Rest maxMunch(ESEQ e){		
		System.out.println(" uppercase: " + e.getClass());
		maxMunch(e.stm);
		return maxMunch(e.exp);
	}

	private Rest maxMunch(MEM e){
		System.out.println(" uppercase: " + e.getClass());
		Rest rr = new Rest();
		Rest r = maxMunch(e.exp);
		rr.addSrc(r.dst.head);
		rr.addDst(new Temp());
		defineRest("mov `d0, [`s0]", rr, Rest.OPER);
		return rr;
	}

	private Rest maxMunch(NAME e){		
		Rest r = new Rest();
		r.label = e.label;
		System.out.println(" NAME: " + e.label);
		return r;
	}

	private Rest maxMunch(TEMP e){		
		System.out.println(" uppercase: " + e.getClass());
		Rest r = new Rest();
		r.addDst(e.temp);
		return r;}

	private void defineRest(String cmd, Rest r, int type){
		//r.cmd = cmd;
		//r.type = type;
		//
		//if (info == null)
		//		last = info = new List<Rest>(r, null);
		//else 
		//		last = last.tail = new List<Rest>(r, null);

		Rest rr = new Rest();
		rr.clone(r);
		rr.cmd = cmd;
		rr.type = type;
		
		if (info == null)
			last = info = new List<Rest>(rr, null);
		else 
			last = last.tail = new List<Rest>(rr, null);
	}

	public void dump(){
		System.out.println(" ** DUMP START **");
		for (List<Rest> r = info; r != null; r = r.tail)
			System.out.println(r.head.cmd);
		System.out.println(" ** DUMP END **");
	}
}
    
