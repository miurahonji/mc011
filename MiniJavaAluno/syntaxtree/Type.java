package syntaxtree;
import semant.Env;
import symbol.ClassInfo;
import symbol.Symbol;
import syntaxtree.IntegerType;
import syntaxtree.IntArrayType;
import syntaxtree.BooleanType;

public abstract class Type extends Absyn
{
    public boolean isComparable(Type t)
    {
        return t.getClass() == this.getClass();
    }

    public boolean compatible(Env e, Type source)
    {
		if ( this instanceof IntegerType )
			return source instanceof IntegerType;
		else if ( this instanceof IntArrayType )
			return source instanceof IntArrayType;
		else if ( this instanceof BooleanType )
			return source instanceof BooleanType;
		else if ( ! (source instanceof IdentifierType) )
			return false;

		Symbol nc1 = Symbol.symbol(((IdentifierType)this).name);
		Symbol nc2 = Symbol.symbol(((IdentifierType)source).name);

		ClassInfo c1 = e.classes.get(nc1);
		ClassInfo c2 = e.classes.get(nc2);

		return compatible(e, c2, c1);
    }

	public boolean compatible(Env e, ClassInfo s, ClassInfo d)
	{
		if (s == null)
			return false;

		while (d != null){
			if (s.name == d.name)
				return true;
			d = d.base;
		}
		return false;
	}
    
	public Type(int l, int r)
	{
		super(l, r);
	}
}
