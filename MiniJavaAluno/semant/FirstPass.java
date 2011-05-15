package semant;

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
import semant.Env;
import util.List;
import visitor.Visitor;

import symbol.ClassInfo;
import symbol.MethodInfo;
import symbol.Symbol;
import symbol.VarInfo;
import syntaxtree.Type;
import java.util.Hashtable;
import java.util.Vector;

public class FirstPass implements Visitor
{
	private Env e;
	private ClassInfo lastClass;
	private MethodInfo lastMethod;
	private VarInfo lastVar;
	private Symbol lastIdentifier;
	private Symbol lastIdentifierType;
	private Type lastType;
	private Hashtable<String, Vector<ClassInfo>> toDoBase;

	private FirstPass()
	{
		super();
		toDoBase = new Hashtable();
	}

	public static void FirstPass(Env env, Program p)
	{
		FirstPass f = new FirstPass();
		f.e = env;
		f.visit(p);
	}

	public void visit(Program node)
	{
		node.mainClass.accept(this);
		for ( List<ClassDecl> aux = node.classList; aux != null; aux = aux.tail )
			aux.head.accept(this);
	}

	public void visit(MainClass node)
	{
		Symbol s = Symbol.symbol(node.className.toString());
		ClassInfo ci = new ClassInfo(s);

		node.className.accept(this);
		node.s.accept(this);
		
		if (!e.classes.put(s, ci))
			e.err.Print(new Object[]{"Main class' name '" + node.className +
					"' was already taken.", "Line " + node.className.line +
					", row " + node.className.row });
	}

	public void visit(ClassDeclSimple node)
	{
		processClassDecl(node, null);
	}

	public void visit(ClassDeclExtends node)
	{
		node.superClass.accept(this);
		processClassDecl(node, lastIdentifier);
	}

	public void processClassDecl(ClassDecl node, Symbol base)
	{
		node.name.accept(this);
		
		lastClass = new ClassInfo(lastIdentifier);

		if (!e.classes.put(lastClass.name, lastClass))
			e.err.Print(new Object[]{"Class named '" + node.name + "' was already added",
					"Line " + node.name.line + ", row " + node.name.row });

		for ( List<VarDecl> vars = node.varList; vars != null; vars = vars.tail )
			vars.head.accept(this);

		for ( List<MethodDecl> methods = node.methodList; methods != null; methods = methods.tail )
			methods.head.accept(this);

		boolean checkToDo = true;
		if (base != null)
		{
			ClassInfo baseClass = e.classes.get(base);
			if (baseClass == null)
			{
				Vector<ClassInfo> inherited = toDoBase.get(base.toString());
				if (inherited == null)
				{
					toDoBase.put(base.toString(), new Vector<ClassInfo>());
					inherited = toDoBase.get(base.toString());
				}
				inherited.add(lastClass);
				checkToDo = false;
			}
			else
				lastClass.setBase(baseClass);
		}

		if (checkToDo)
			setBaseToDo(node.name.s.toString());

		// restoring this state
		lastClass = null;
	}

	public void setBaseToDo(String name)
	{
		Vector<ClassInfo> aux = toDoBase.get(name);
		if (aux != null)
			for ( int cl = 0; cl < aux.size(); cl++ )
			{
				aux.get(cl).setBase(lastClass);
				setBaseToDo(aux.get(cl).name.toString());
			}
			toDoBase.remove(name);
		return;
	}

	public void visit(VarDecl node)
	{
		node.type.accept(this);
		node.name.accept(this);

		VarInfo v = new VarInfo(lastType, lastIdentifier);
		// Is this VarDecl from Method (local variable) or from Class (attribute)
		if (lastMethod != null)
		{
			// running from visit(MethodDecl)
			if (!lastMethod.addLocal(v))
			{
				e.err.Print(new Object[]{"Variable's name '" + lastIdentifier + 
						"' was already taken on scope of method '" + lastClass.name + "." +
						lastMethod.name + "'", "Line " + node.line + ", row " + node.row });
			}

		}
		else if (!lastClass.addAttribute(v))
		{
			// running from processClassDecl(ClassDecl)
			e.err.Print(new Object[]{"Attribute's name '" + lastIdentifier +
					"' was already taken on class '" + lastClass.name + "'",
					"Line " + node.line + ", row " + node.row });
		}
	}

	public void visit(MethodDecl node)
	{
		node.returnType.accept(this);
		node.name.accept(this);

		lastMethod = new MethodInfo(lastType, lastIdentifier, lastClass.name);
		
		for ( List<Formal> f = node.formals; f != null; f = f.tail )
		{
			f.head.accept(this);
		}

		
		for ( List<VarDecl> l = node.locals; l != null; l = l.tail )
			l.head.accept(this);
		
		if (!lastClass.addMethod(lastMethod))
			e.err.Print(new Object[]{"Method's name '" + lastMethod.name + "' was already added",
					"Line " + lastMethod.type.line + ", row " + lastMethod.type.row });

		// restoring this state
		lastMethod = null;
	}

	public void visit(Formal node)
	{
		node.type.accept(this);
		node.name.accept(this);

		VarInfo v = new VarInfo(lastType, lastIdentifier);
		if (!lastMethod.addFormal(v))
			e.err.Print(new Object[]{"Formal's '" + v + "' was already added",
					"Line " + lastMethod.type.line + ", row " + lastMethod.type.row });
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
		return;
	}

	public void visit(If node)
	{
		return;
	}

	public void visit(While node)
	{
		return;
	}

	public void visit(Print node)
	{
		return;
	}

	public void visit(Assign node)
	{
		return;
	}

	public void visit(ArrayAssign node)
	{
		return;
	}

	public void visit(And node)
	{
		return;
	}

	public void visit(LessThan node)
	{
		return;
	}

	public void visit(Equal node)
	{
		return;
	}

	public void visit(Plus node)
	{
		return;
	}

	public void visit(Minus node)
	{
		return;
	}

	public void visit(Times node)
	{
		return;
	}

	public void visit(ArrayLookup node)
	{
		return;
	}

	public void visit(ArrayLength node)
	{
		return;
	}

	public void visit(Call node)
	{
		return;
	}

	public void visit(IntegerLiteral node)
	{
		return;
	}

	public void visit(True node)
	{
		return;
	}

	public void visit(False node)
	{
		return;
	}

	public void visit(This node)
	{
		return;
	}

	public void visit(NewArray node)
	{
		return;
	}

	public void visit(NewObject node)
	{
		return;
	}

	public void visit(Not node)
	{
		return;
	}

	public void visit(IdentifierExp node)
	{
		return;
	}

	public void visit(Identifier node)
	{
		lastIdentifier = Symbol.symbol(node.s.toString());
	}

	public void visit(Type node)
	{
		return;
	}
}
