class Principal
{
    public static void main(String[] args)
    {
        {
           a[new Obj()] = false;
        }
    }
}

class Obj
{
    public int qual()
    {
		a[new Obj()] = false;
		return 0;
    }
}
