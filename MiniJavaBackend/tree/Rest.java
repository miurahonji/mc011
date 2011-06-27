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
	public void clone(Rest r) {
		for (List<Temp> x = r.src; x != null; x = x.tail)
			addSrc(x.head);

		for (List<Temp> x = r.dst; x != null; x = x.tail)
			addDst(x.head);
		
		if (r.label != null)
			label = new Label(r.label.toString());

		for (List<Label> x = r.targets; x != null; x = x.tail)
			addTarget(x.head);

		if (r.jump != null)
			jump = new Targets(r.jump.labels);

		value = r.value;
		// Esses dois não precisa
		// sim, pq sao sobreescritos.. mas eu deixem, clone ẽ clone ué... Mas vamos tirar e ver o que acontece, pra mim tambem não é isso
		// ok
		// e q a gente abandonasse a idẽia do clone e revesse cada chamada ao defineRest!? muito trsampo nẽ... 
		// Nem eh, mas daria o mesmo pau, eu ac
		// srry, vai!
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

	public void addTarget(Label s)
	{
		if (targets == null)
			targets = new List<Label>(s, null);
		else
			targets.tail = new List<Label>(s, null);
	}
}

