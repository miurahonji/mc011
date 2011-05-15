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
		node.mainClass.accept(this);
		for ( List<ClassDecl> aux = node.classList; aux != null; aux = aux.tail )
		{
			aux.head.accept(this);
		}
	}

	public void visit(MainClass node)
	{
		node.className.accept(this);
		lastClass = e.classes.get(lastIdentifier);
		node.mainArgName.accept(this);
		node.s.accept(this);
	}

	public void visit(ClassDeclSimple node)
	{
		node.name.accept(this);
		lastClass = e.classes.get(lastIdentifier);
		
		for ( List<VarDecl> vars = node.varList; vars != null; vars = vars.tail )
			vars.head.accept(this);

		for ( List<MethodDecl> methods = node.methodList; methods != null; methods = methods.tail )
			methods.head.accept(this);
		
	}

	public void visit(ClassDeclExtends node)
	{
		node.name.accept(this);
		lastClass = e.classes.get(lastIdentifier);

		if (!lastClass.checkCyclicInherit()){
			e.err.Print(new Object[]{
				"Cyclic Inheritance detected on " + node.name, 
				"Line " + node.name.line + ", row " + node.name.row });
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
		node.name.accept(this);
		String name = lastIdentifier.toString();

		lastMethod = lastClass.methods.get(Symbol.symbol(name));
		if (lastMethod == null)
			return;

		node.returnExp.accept(this);
		String returnExp = lastType.toString();

		if (!lastType.compatible(e, lastMethod.type))
		{
			e.err.Print(new Object[]{
				"Incompatible types with return type '" + returnExp + "' and expected type '" + lastMethod.type + "'",
				"Line " + node.name.line + ", row " + node.name.row });
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
				"Incompatible types, " + expType + " is not compatible to " + expType,
				"Line " + node.line + ", row " + node.row });
	}

	public void visit(ArrayAssign node)
	{
		node.var.accept(this);

		node.index.accept(this);
		if (!lastType.compatible(e, new IntegerType(node.line, node.row)))
			e.err.Print(new Object[]{
				"Index type must be a integer and found: " + lastType,
				"Line " + node.line + ", row " + node.row });

		node.value.accept(this);
		if (!lastType.compatible(e, new IntegerType(node.line, node.row)))
			e.err.Print(new Object[]{
				"Incompatible types, " + lastType + " is not compatible to int",
				"Line " + node.line + ", row " + node.row });
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
				"Left type " + lhsType + " is not compatible with right type " + rhsType,
				"Line " + node.line + ", row " + node.row });
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
				"Left type " + lhsType + " is not compatible with right type " + rhsType,
				"Line " + node.line + ", row " + node.row });
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
				"Left type " + lhsType + " is not compatible with right type " + rhsType,
				"Line " + node.line + ", row " + node.row });
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
		node.method.accept(this);
		String name = lastIdentifier.toString();

		ClassInfo caller = e.classes.get(Symbol.symbol(lastType.toString()));
		if (caller == null)
		{
			e.err.Print(new Object[]{
				"Method " + name + " not found at class " + lastType,
				"Line " + node.line + ", row " + node.row });
			return;
		}

		String parent = caller.name.toString();
		lastMethod = caller.methods.get(Symbol.symbol(name));
		if (lastMethod == null)
		{
			e.err.Print(new Object[]{
				"Method " + name + " not found at class " + parent,
				"Line " + node.line + ", row " + node.row });
			return;
		}

		List<Exp> aux = node.actuals;
		List<VarInfo> aux2 = lastMethod.formals;
		while (aux != null && aux2 != null)
		{
			aux.head.accept(this);
			if (lastType.toString() != aux2.head.type.toString())
				e.err.Print(new Object[]{
					"Parameter called as " + lastType + " is expected as " + aux2.head.type + " at " + name + " method",
					"Line " + node.line + ", row " + node.row });

			aux = aux.tail;
			aux2 = aux2.tail;
		}

		if (aux != null || aux2 != null)
			e.err.Print(new Object[]{
				"Parameter list size is different at " + name + ", expects " + lastMethod.formals.size() + " parameters",
				"Line " + node.line + ", row " + node.row });

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
				"Class " + name + " is not defined.",
				"Line " + node.line + ", row " + node.row });
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
		if (lastMethod == null)
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
			"Variable " + node.s + " not declared at " + lastMethod.name,
			"Line " + node.line + ", row " + node.row });
		lastType = new IntegerType(node.line, node.row);
	}
}
