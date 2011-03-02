import StraightLine.*;
import java.lang.Math;

public class Example {

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

        System.out.println("maxargs == " + maxargs(prog));
        System.out.println("Exiting...");
    }

    public static int maxargs(Stm prog) {
        /**
         * returns the size of the argument of the biggest print inside of this.prog
         */
        if (prog instanceof CompoundStm) {
            CompoundStm p = (CompoundStm)prog;
            return (Math.max(maxargs(p.stm1), maxargs(p.stm2)));
        }
        else if (prog instanceof AssignStm) {
            AssignStm p = (AssignStm)prog;
            return maxargs(p.exp);
        }
        else if (prog instanceof PrintStm) {
            PrintStm p = (PrintStm)prog;
            // this is going to return something interesting...
            return maxargs(p.exps);
        }
        else {
            return -1;
        }
    }

    public static int maxargs(Exp prog) {
        if (prog instanceof OpExp) {
            OpExp p = (OpExp)prog;
            return (Math.max(maxargs(p.left), maxargs(p.right)));
        }
        else if (prog instanceof EseqExp) {
            EseqExp p = (EseqExp)prog;
            return (Math.max(maxargs(p.stm), maxargs(p.exp)));
        }
        else {
            return -1;
        }
    }

    public static int maxargs(ExpList list) {
        // initialize the counting of the size of this list and its nested prints
        return maxargs(list, 1);
    }

    public static int maxargs(ExpList exp_list, int current_size) {
        // found it! let's count this chain
        if (exp_list instanceof PairExpList) {
            PairExpList pair_list = (PairExpList)exp_list;
            int h = maxargs(pair_list.head);
            int t = maxargs(pair_list.tail, current_size+1);
            return (Math.max(h, t));
        }
        else
        {
            return current_size;
        }
    }

}

