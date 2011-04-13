package visitor;

import syntaxtree.And;
import syntaxtree.ArrayAssign;
import syntaxtree.ArrayLength;
import syntaxtree.ArrayLookup;
import syntaxtree.Assign;
import syntaxtree.Block;
import syntaxtree.BooleanType;
import syntaxtree.Call;
import syntaxtree.ClassDeclExtends;
import syntaxtree.ClassDeclSimple;
import syntaxtree.Equal;
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
import syntaxtree.This;
import syntaxtree.Times;
import syntaxtree.True;
import syntaxtree.VarDecl;
import syntaxtree.While;

public interface Visitor
{
	public void visit(Program node);
	public void visit(MainClass node);
	public void visit(ClassDeclSimple node);
	public void visit(ClassDeclExtends node);
	public void visit(VarDecl node);
	public void visit(MethodDecl node);
	public void visit(Formal node);
	public void visit(IntArrayType node);
	public void visit(BooleanType node);
	public void visit(IntegerType node);
	public void visit(IdentifierType node);
	public void visit(Block node);
	public void visit(If node);
	public void visit(While node);
	public void visit(Print node);
	public void visit(Assign node);
	public void visit(ArrayAssign node);
	public void visit(And node);
	public void visit(LessThan node);
	public void visit(Equal node);
	public void visit(Plus node);
	public void visit(Minus node);
	public void visit(Times node);
	public void visit(ArrayLookup node);
	public void visit(ArrayLength node);
	public void visit(Call node);
	public void visit(IntegerLiteral node);
	public void visit(True node);
	public void visit(False node);
	public void visit(This node);
	public void visit(NewArray node);
	public void visit(NewObject node);
	public void visit(Not node);
	public void visit(IdentifierExp node);
	public void visit(Identifier node);
}
