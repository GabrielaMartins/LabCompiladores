package ast;

public class TypeVoid extends Type {
    
    public TypeVoid() {
        super("void");
    }
    
    public String getCname() {
    	return "void";
    }
    
	@Override
	public int compareTo(Type o) {
		if (o == null) {
			return -1;
		}
		
		return this.getName().compareTo(o.getName());
	}
}
