package ast;

public class TypeString extends Type {
    
    public TypeString() {
        super("String");
    }
    
    public String getCname() {
    	return "char *";
    }
    
	@Override
	public int compareTo(Type o) {
		if (o == null) {
			return -1;
		}
		
		return this.getName().compareTo(o.getName());
	}
}
