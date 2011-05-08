package symbol;

import frame.Access;
import syntaxtree.Type;
import syntaxtree.IntArrayType;
import syntaxtree.BooleanType;
import syntaxtree.IntegerType;

public class VarInfo
{
	public Type type;
	public Symbol name;
	
    public Access access;
    
	public VarInfo(Type t, Symbol s)
	{
		super();
		
		type = t;
		name = s;
	}

	public String toString()
	{
		if (type instanceof IntArrayType)
			return ((IntArrayType)type).toString() + name.toString();
		else if (type instanceof BooleanType)
			return ((BooleanType)type).toString() + name.toString();
		else  // if (type instanceof IntegerType)
			return ((IntegerType)type).toString() + name.toString();
	}
}
