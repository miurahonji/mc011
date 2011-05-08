package util;

public class List<E>
{
	public E head;
	public List<E> tail;
	
	public List(E h, List<E> t)
	{
        if ( h == null )
            throw new Error();
        
		head = h;
		tail = t;
	}
    
    public int size()
    {
        if ( tail == null )
            return 1;
        
        return 1 + tail.size();
    }

	public boolean contains(E e)
	{
		if (e == head)
			return true;

		for ( List<E> es = tail; es != null; es = es.tail )
			if (e == es)
				return true;

		return false;
	}
}
