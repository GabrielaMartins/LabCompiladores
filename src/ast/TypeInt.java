package ast;

public class TypeInt extends Type {
	   
    public TypeInt() {
        super("int");
    }
    
    public String getCname() {
    	return "int";
    }
    
	@Override
	public int compareTo(Type o) {
		if (o == null) {
			return -1;
		}
		
		return this.getName().compareTo(o.getName());
	}
}
