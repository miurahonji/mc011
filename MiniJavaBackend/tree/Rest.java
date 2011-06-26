package tree;

import assem.Instr;
import assem.Targets;
import tree.Stm;
import temp.Temp;
import temp.Label;
import util.List;

public class Rest {
	public String cmd;
	public List<Temp> src, dst;
	public Label label;
	public List<Label> targets;
	public Targets jump;
	public long value;

	// LABEL = 0; MOVE = 1; OPER = 2
	public static final int LABEL = 0;
	public static final int MOVE = 1;
	public static final int OPER = 2;
	public static final int OPER_TARGET = 3;
	public int type;

	public Rest() {
		cmd = "";
		src = null;
		dst = null;
		label = null;
		targets = null;
		jump = null;
		value = 0;
		type = -1;
	}

	public void addSrc(Temp s)
	{
		if (src == null)
			src = new List<Temp>(s, null);
		else
			src.tail = new List<Temp>(s, null);
	}

	public void addDst(Temp s)
	{
		if (dst == null)
			dst = new List<Temp>(s, null);
		else
			dst.tail = new List<Temp>(s, null);
	}
}

