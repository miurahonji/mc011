package semant;

import errors.ErrorEchoer;
import syntaxtree.And;
import syntaxtree.ArrayAssign;
import syntaxtree.ArrayLength;
import syntaxtree.ArrayLookup;
import syntaxtree.Assign;
import syntaxtree.Block;
import syntaxtree.BooleanType;
import syntaxtree.Call;
import syntaxtree.ClassDecl;
import syntaxtree.ClassDeclExtends;
import syntaxtree.ClassDeclSimple;
import syntaxtree.Equal;
import syntaxtree.Exp;
import syntaxtree.False;
import syntaxtree.Formal;
import syntaxtree.Identifier;
import syntaxtree.IdentifierExp;
import syntaxtree.IdentifierType;
import syntaxtree.If;
import syntaxtree.IntArrayType;
import syntaxtree.IntegerLiteral;
import syntaxtree.IntegerType;
import syntaxtree.LessThan;
import syntaxtree.MainClass;
import syntaxtree.MethodDecl;
import syntaxtree.Minus;
import syntaxtree.NewArray;
import syntaxtree.NewObject;
import syntaxtree.Not;
import syntaxtree.Plus;
import syntaxtree.Print;
import syntaxtree.Program;
import syntaxtree.Statement;
import syntaxtree.This;
import syntaxtree.Times;
import syntaxtree.True;
import syntaxtree.VarDecl;
import syntaxtree.While;
import util.List;
import visitor.Visitor;
import semant.Env;
import symbol.ClassInfo;
import symbol.MethodInfo;
import symbol.Symbol;
import symbol.VarInfo;
import syntaxtree.Type;
import java.util.Enumeration;

public class SecondPass implements Visitor
{
	private Env e;
	private ClassInfo lastClass;
	private MethodInfo lastMethod;
	private VarInfo lastVar;
	private Symbol lastIdentifier;
	private Symbol lastIdentifierType;
	private Type lastType;

    private SecondPass()
    {
        super();
    }

	public static void SecondPass(Env env, Program p)
	{

		SecondPass s = new SecondPass();
		s.e = env;
		s.visit(p);
	}

	public void visit(Program node)
	{
		Enumeration<Symbol> classes = e.classes.keys();
		for (Enumeration<Symbol> k = classes; k.hasMoreElements() ;) 
		{
			e.classes.get(k.nextElement()).checkOverLoading(e);
		}

		node.mainClass.accept(this);
		for ( List<ClassDecl> aux = node.classList; aux != null; aux = aux.tail )
		{
			aux.head.accept(this);
		}
	}

	public void visit(MainClass node)
	{
		lastIdentifier = Symbol.symbol(node.className.s.toString());
		lastClass = e.classes.get(lastIdentifier);
		node.mainArgName.accept(this);
		node.s.accept(this);
	}

	public void visit(ClassDeclSimple node)
	{
		lastIdentifier = Symbol.symbol(node.name.s.toString());
		lastClass = e.classes.get(lastIdentifier);
		
		for ( List<VarDecl> vars = node.varList; vars != null; vars = vars.tail )
			vars.head.accept(this);

		for ( List<MethodDecl> methods = node.methodList; methods != null; methods = methods.tail )
			methods.head.accept(this);
		
	}

	public void visit(ClassDeclExtends node)
	{
		lastIdentifier = Symbol.symbol(node.name.s.toString());
		lastClass = e.classes.get(lastIdentifier);

		if (!lastClass.checkCyclicInherit()){
			e.err.Print(new Object[]{
				"[" + node.name.line + "," + node.name.row + "] " +
				"Cyclic Inheritance detected on " + node.name});
		}

		for ( List<VarDecl> vars = node.varList; vars != null; vars = vars.tail )
			vars.head.accept(this);
		
		for ( List<MethodDecl> methods = node.methodList; methods != null; methods = methods.tail )
			methods.head.accept(this);
		
	}

	public void visit(VarDecl node)
	{
		node.type.accept(this);
		node.name.accept(this);
	}

	public void visit(MethodDecl node)
	{
		lastIdentifier = Symbol.symbol(node.name.s.toString());
		String name = lastIdentifier.toString();

		StringBuffer formalsString = new StringBuffer();
		for ( List<Formal> vars = node.formals; vars != null; vars = vars.tail )
			formalsString.append(vars.head.type);

		lastMethod = lastClass.methodsByName.get(Symbol.symbol(name + "@" + formalsString));
		if (lastMethod == null)
			return;

		node.returnExp.accept(this);
		String returnExp = lastType.toString();

		if (!lastType.compatible(e, lastMethod.type))
		{
			e.err.Print(new Object[]{
				"[" + node.name.line + "," + node.name.row + "] " +
				"Incompatible types with return type '" + returnExp + "' and expected type '" + lastMethod.type + "'"});
			return;
		}
		
		for ( List<VarDecl> l = node.locals; l != null; l = l.tail )
			l.head.accept(this);
		
		for ( List<Statement> s = node.body; s != null; s = s.tail )
			s.head.accept(this);
	}

	public void visit(Formal node)
	{
		node.type.accept(this);
		node.name.accept(this);
	}

	public void visit(IntArrayType node)
	{
		lastType = node;
	}

	public void visit(BooleanType node)
	{
		lastType = node;
	}

	public void visit(IntegerType node)
	{
		lastType = node;
	}

	public void visit(IdentifierType node)
	{
		lastType = node;
	}

	public void visit(Block node)
	{
		
		for ( List<Statement> aux = node.body; aux != null; aux = aux.tail )
			aux.head.accept(this);
		
	}

	public void visit(If node)
	{
		node.condition.accept(this);
		node.thenClause.accept(this);
		if ( node.elseClause != null )
		{
			node.elseClause.accept(this);
		}
	}

	public void visit(While node)
	{
		node.condition.accept(this);
		node.body.accept(this);
	}

	public void visit(Print node)
	{
		node.exp.accept(this);
	}

	public void visit(Assign node)
	{
		node.var.accept(this);
		Type varType = lastType;
		node.exp.accept(this);
		Type expType = lastType;
		
		if (!varType.compatible(e, expType))
			e.err.Print(new Object[]{
				"[" + node.line + "," + node.row + "] " +
				"Incompatible types, " + expType + " is not compatible to " + expType});
	}

	public void visit(ArrayAssign node)
	{
		node.var.accept(this);

		node.index.accept(this);
		if (!lastType.compatible(e, new IntegerType(node.line, node.row)))
			e.err.Print(new Object[]{
				"[" + node.line + "," + node.row + "] " +
				"Index type must be a integer and found: " + lastType});

		node.value.accept(this);
		if (!lastType.compatible(e, new IntegerType(node.line, node.row)))
			e.err.Print(new Object[]{
				"[" + node.line + "," + node.row + "] " +
				"Incompatible types, " + lastType + " is not compatible to int"});
	}

	public void visit(And node)
	{
		node.lhs.accept(this);
		node.rhs.accept(this);
	}

	public void visit(LessThan node)
	{
		node.lhs.accept(this);
		node.rhs.accept(this);
	}

	public void visit(Equal node)
	{
		node.lhs.accept(this);
		node.rhs.accept(this);
	}

	public void visit(Plus node)
	{
		node.lhs.accept(this);
		Type lhsType = lastType;

		node.rhs.accept(this);
		Type rhsType = lastType;

		if (rhsType.toString() != lhsType.toString())
			e.err.Print(new Object[]{
				"[" + node.line + "," + node.row + "] " +
				"Left type " + lhsType + " is not compatible with right type " + rhsType });
		lastType = rhsType;

	}

	public void visit(Minus node)
	{
		node.lhs.accept(this);
		Type lhsType = lastType;

		node.rhs.accept(this);
		Type rhsType = lastType;

		if (rhsType.toString() != lhsType.toString())
			e.err.Print(new Object[]{
				"[" + node.line + "," + node.row + "] " +
				"Left type " + lhsType + " is not compatible with right type " + rhsType });
		lastType = rhsType;
	}

	public void visit(Times node)
	{
		node.lhs.accept(this);
		Type lhsType = lastType;

		node.rhs.accept(this);
		Type rhsType = lastType;

		if (rhsType.toString() != lhsType.toString())
			e.err.Print(new Object[]{
				"[" + node.line + "," + node.row + "] " +
				"Left type " + lhsType + " is not compatible with right type " + rhsType });
		lastType = rhsType;
	}

	public void visit(ArrayLookup node)
	{
		node.array.accept(this);
		node.index.accept(this);
	}

	public void visit(ArrayLength node)
	{
		node.array.accept(this);
	}

	public void visit(Call node)
	{
		node.object.accept(this);
		lastIdentifier = Symbol.symbol(node.method.s.toString());
		String name = lastIdentifier.toString();

		ClassInfo caller = e.classes.get(Symbol.symbol(lastType.toString()));
		if (caller == null)
		{
			e.err.Print(new Object[]{
				"[" + node.line + "," + node.row + "] " +
				"Method " + name + " not found at class " + lastType });
			return;
		}

		StringBuffer formalsString = new StringBuffer();
		for ( List<Exp> vars = node.actuals; vars != null; vars = vars.tail )
		{
			vars.head.accept(this);
			formalsString.append(lastType);
		}

		String parent = caller.name.toString();
		lastMethod = caller.methodsByName.get(Symbol.symbol(name + "@" + formalsString));
		if (lastMethod == null)
		{
			e.err.Print(new Object[]{
				"[" + node.line + "," + node.row + "] " +
				"Method " + name + " not found at class " + parent });
			return;
		}

		List<Exp> aux = node.actuals;
		List<VarInfo> aux2 = lastMethod.formals;
		while (aux != null && aux2 != null)
		{
			aux.head.accept(this);
			if (lastType.toString() != aux2.head.type.toString())
				e.err.Print(new Object[]{
					"[" + node.line + "," + node.row + "] " +
					"Parameter called as " + lastType + " is expected as " + aux2.head.type + " at " + name + " method" });

			aux = aux.tail;
			aux2 = aux2.tail;
		}

		if (aux != null || aux2 != null)
			e.err.Print(new Object[]{
				"[" + node.line + "," + node.row + "] " +
				"Parameter list size is different at " + name + ", expects " + lastMethod.formals.size() + " parameters" });

		lastType = lastMethod.type;
	}

	public void visit(IntegerLiteral node)
	{
		lastType = new IntegerType(node.line, node.row);
	}

	public void visit(True node)
	{
		lastType = new BooleanType(node.line, node.row);
	}

	public void visit(False node)
	{
		lastType = new BooleanType(node.line, node.row);
	}

	public void visit(This node)
	{
		lastType = new IdentifierType(node.line, node.row, lastClass.name.toString());
	}

	public void visit(NewArray node)
	{
		node.size.accept(this);
	}

	public void visit(NewObject node)
	{
		node.className.accept(this);
		String name = node.className.toString();
		if (e.classes.get(Symbol.symbol(name)) != null)
			lastType = new IdentifierType(node.line, node.row, name);
		else
		{
			e.err.Print(new Object[]{
				"[" + node.line + "," + node.row + "] " +
				"Class " + name + " is not defined." });
			System.exit(1);
		}
	}

	public void visit(Not node)
	{
		node.exp.accept(this);
	}

	public void visit(IdentifierExp node)
	{
		node.name.accept(this);
	}

	public void visit(Identifier node)
	{
		lastIdentifier = Symbol.symbol(node.s.toString());
		if (lastMethod == null || lastClass == null)
			return;

		for (List<VarInfo> i = lastMethod.locals; i != null; i = i.tail)
			if (i.head.name == lastIdentifier)
			{
				lastType = i.head.type;
				return;
			}

		for (List<VarInfo> i = lastMethod.formals; i != null; i = i.tail)
			if (i.head.name == lastIdentifier)
			{
				lastType = i.head.type;
				return;
			}

		VarInfo i = lastClass.attributes.get(lastIdentifier);
		if (i != null)
		{
			lastType = i.type;
			return;
		}
		
		e.err.Print(new Object[]{
			"[" + node.line + "," + node.row + "] " +
			"Variable " + node.s + " not declared at " + lastMethod.name });
		lastType = new IntegerType(node.line, node.row);
	}
}
