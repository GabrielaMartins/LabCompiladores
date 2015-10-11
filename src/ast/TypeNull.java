package ast;

public class TypeNull extends Type {
	
	public TypeNull() {
	    super("null");
	}
	
	public String getCname() {
		return "NULL";
	}
	
	@Override
	public int compareTo(Type o) {
		if (o == null) {
			return -1;
		}
		
		return this.getName().compareTo(o.getName());
	}
}
