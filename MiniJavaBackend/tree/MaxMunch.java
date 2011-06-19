package tree;

import assem.Instr;
import util.List;
import temp.Temp;

public class MaxMunch
{
	public List<Rest> info = null;

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

	private Rest maxMunch(Stm s){return new Rest();}
	private Rest maxMunch(CJUMP s){return new Rest();}
	private Rest maxMunch(EXPSTM s){
		//FIXME
		return maxMunch(s.exp);
	}

	private Rest maxMunch(JUMP s){
		//FIXME
		Rest r = new Rest();
		r.targets = s.targets;
		r.dst = new Temp();
		r.src = new Temp();
		defineRest("JUMP", r);
		return r;
	}

	private Rest maxMunch(LABEL s){
		//FIXME
		Rest r = new Rest();
		r.label = s.label;
		System.out.println("gu" + s.getClass() + s.label);
		defineRest("LABEL", r);
		return r;
	}

	private Rest maxMunch(SEQ s){return new Rest();}

    private Rest maxMunch(MOVE s) {
		System.out.println("gu" + s.getClass());
		Rest r = new Rest();
		if (s.dst instanceof TEMP)
		{
			TEMP t = (TEMP)(s.dst);
			r.dst = t.temp;
		}
		else
		{
			Rest rr = maxMunch(s.dst);
			r.dst = rr.dst;
		}

		if (s.src instanceof TEMP)
		{
			TEMP t = (TEMP)(s.src);
			r.src = t.temp;
		}
		else
		{
			Rest rr = maxMunch(s.src);
			r.src = rr.dst;

		}

		defineRest("MOV", r);
		return r;
	}


	private Rest maxMunch(Exp e){		
		System.out.println("gu" + e.getClass());
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
		System.out.println("gu" + e.getClass());
		return new Rest();
	}

	private Rest maxMunch(CALL e){		
		//FIXME
		Rest r = new Rest();
		r.dst = new Temp();
		r.src = new Temp();
		System.out.println("gu" + e.getClass() + r.src + r.dst);
		defineRest("CALL", r);
		return r;
	}

	private Rest maxMunch(CONST e){		
		System.out.println("gu" + e.getClass());
		return new Rest();
	}

	private Rest maxMunch(ESEQ e){		
		System.out.println("gu" + e.getClass());
		return new Rest();
	}

	private Rest maxMunch(MEM e){		
		System.out.println("gu" + e.getClass());
		return new Rest();}
	private Rest maxMunch(NAME e){		
		System.out.println("gu" + e.getClass());
		return new Rest();
	}
	private Rest maxMunch(TEMP e){		
		System.out.println("gu" + e.getClass());
		return new Rest();}

	private void defineRest(String cmd, Rest r){
		r.cmd = cmd;
		
		if (info == null)
			info = new List<Rest>(r, null);
		else
			info.tail = new List<Rest>(r, null);
	}
}
    
