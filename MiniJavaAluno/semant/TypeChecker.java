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

public class TypeChecker implements Visitor
{
    private TypeChecker()
    {
        super();
    }

    public static Env TypeCheck(ErrorEchoer err, Program p)
    {		
    	TypeChecker t = new TypeChecker();
    	t.visit(p);
    	return null;
    }

	public void visit(Program node)
	{
		node.mainClass.accept(this);
		for ( List<ClassDecl> aux = node.classList; aux != null; aux = aux.tail )
			aux.head.accept(this);
	}

	public void visit(MainClass node)
	{
		node.className.accept(this);
		
		node.mainArgName.accept(this);
		
		node.s.accept(this);
		
	}

	public void visit(ClassDeclSimple node)
	{
		node.name.accept(this);
		
		for ( List<VarDecl> vars = node.varList; vars != null; vars = vars.tail )
			vars.head.accept(this);

		for ( List<MethodDecl> methods = node.methodList; methods != null; methods = methods.tail )
			methods.head.accept(this);
		
	}

	public void visit(ClassDeclExtends node)
	{
		node.name.accept(this);
		node.superClass.accept(this);
		
		for ( List<VarDecl> vars = node.varList; vars != null; vars = vars.tail )
		{
			vars.head.accept(this);
		}
		
		for ( List<MethodDecl> methods = node.methodList.tail; methods != null; methods = methods.tail )
			methods.head.accept(this);
		
	}

	public void visit(VarDecl node)
	{
		node.type.accept(this);
		node.name.accept(this);
	}

	public void visit(MethodDecl node)
	{
		node.returnType.accept(this);
		node.name.accept(this);
		
		for ( List<Formal> f = node.formals; f != null; f = f.tail )
		{
			f.head.accept(this);
			
			if ( f.tail != null )
				return;
		}
		
		
		for ( List<VarDecl> l = node.locals; l != null; l = l.tail )
			l.head.accept(this);
		
		for ( List<Statement> s = node.body; s != null; s = s.tail )
			s.head.accept(this);
		
		
		node.returnExp.accept(this);
		
		
	}

	public void visit(Formal node)
	{
		node.type.accept(this);
		node.name.accept(this);
	}

	public void visit(IntArrayType node)
	{
		return;
	}

	public void visit(BooleanType node)
	{
		return;
	}

	public void visit(IntegerType node)
	{
		return;
	}

	public void visit(IdentifierType node)
	{
		return;
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
		node.exp.accept(this);
	}

	public void visit(ArrayAssign node)
	{
		node.var.accept(this);
		node.index.accept(this);
		node.value.accept(this);
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
		node.rhs.accept(this);
	}

	public void visit(Minus node)
	{
		node.lhs.accept(this);
		node.rhs.accept(this);
	}

	public void visit(Times node)
	{
		node.lhs.accept(this);
		node.rhs.accept(this);
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
		
		
		for ( List<Exp> aux = node.actuals; aux != null; aux = aux.tail )
		{
			aux.head.accept(this);
			
			if ( aux.tail != null )
				return;
		}
		
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
		node.size.accept(this);
	}

	public void visit(NewObject node)
	{
		node.className.accept(this);
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
		return;
	}

}
