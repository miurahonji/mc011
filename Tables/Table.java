import StraightLine.*;

public class Table {
	String id; int value; Table tail;
	Table(String i, int v, Table t) {
		id=i;
		value=v;
		tail=t;
	}

	public int lookup(String key){
		if (this.tail == null)
			return -1;

		if (this.id == key)
			return this.value;

		return this.tail.lookup(key);
	}

	public Table update (String key, int value){
		return new Table(key, value, this);
	}
}
