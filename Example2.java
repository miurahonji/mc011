import StraightLine.*;

public class Example2 {
    public static void main(String[] args) {
        Stm prog = new CompoundStm(
            new AssignStm(
                "a",
                new OpExp(
                    new NumExp(
                        5),
                    OpExp.Plus,
                    new NumExp(3))),
            new CompoundStm(
                new AssignStm(
                    "b",
                    new EseqExp(
                        new PrintStm(
                            new PairExpList(
                                new IdExp(
                                    "a"),
                                new LastExpList(
                                    new OpExp(
                                        new IdExp(
                                            "a"),
                                        OpExp.Minus,
                                        new NumExp(
                                            1))))),
                        new OpExp(
                            new NumExp(
                                10),
                            OpExp.Times,
                            new IdExp(
                                "a")))),
                new PrintStm(
                    new LastExpList(
                        new IdExp(
                            "b")))));

    	Table t = new Table("", 0, null);
        interp(prog, t);
    }

    public static Table interp(Stm s, Table t){
        if (s instanceof CompoundStm) {
            CompoundStm p = (CompoundStm)s;
            Table t2 = interp(p.stm1, t);
            return interp(p.stm2, t2);
        }
        else if (s instanceof AssignStm) {
            AssignStm p = (AssignStm)s;
            IntAndTable int_table = interpExp(p.exp, t);
            return int_table.t.update(p.id, int_table.i);
        }
        else if (s instanceof PrintStm) {
            PrintStm p = (PrintStm)s;
			ExpList l = p.exps;
			while (l instanceof PairExpList){
				PairExpList pair_list = (PairExpList)l;
				IntAndTable int_table = interpExp(pair_list.head, t);
				System.out.println(int_table.t.id + "="+ int_table.i);
				l = pair_list.tail;
			}
			LastExpList last_list = (LastExpList)l;
			IntAndTable int_table = interpExp(last_list.head, t);
			System.out.println(int_table.t.id + "="+ int_table.i);
			return t;
        }
        else {
        	System.out.println("Error Stm not found!!\n");
			return null;
        }
    }

	public static IntAndTable interpExp(Exp e, Table t){
        if (e instanceof OpExp) {
            OpExp p = (OpExp)e;
            IntAndTable left = interpExp(p.left, t);
            IntAndTable right = interpExp(p.right, t);
            switch (p.oper) {
            	case OpExp.Plus:
            		return new IntAndTable(left.i+right.i, t);
            	case OpExp.Minus:
            		return new IntAndTable(left.i-right.i, t);
            	case OpExp.Times:
            		return new IntAndTable(left.i*right.i, t);
            	case OpExp.Div:
            		return new IntAndTable(left.i/right.i, t);
			}
        }
        else if (e instanceof EseqExp) {
            EseqExp p = (EseqExp)e;
            Table t2 = interp(p.stm, t);
            IntAndTable t3 = interpExp(p.exp, t2);
        	return t3;
        }
        else if (e instanceof NumExp) {
            NumExp p = (NumExp)e;
            return new IntAndTable(p.num, t);
        }
        else if (e instanceof IdExp) {
            IdExp p = (IdExp)e;
            return new IntAndTable(t.lookup(p.id), t);
        }
		return null;
	}
}
