package tree;

import assem.Instr;
import tree.Stm;
import temp.Temp;
import temp.Label;
import util.List;

public class Rest {
	public String cmd;
    public Temp src, dst;
	public Label label;
	public List<Label> targets;

	public Rest() {
		cmd = "";
		src = null;
		dst = null;
		label = null;
		targets = null;
	}
}

