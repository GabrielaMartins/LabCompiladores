/*
 * Gabriela de Jesus Martins	- 489689
 * Valdeir Soares Perozim		- 489786
 */

package ast;

public class TypeUndefined extends Type {
    // variables that are not declared have this type
    public TypeUndefined() { 
    	super("undefined");
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
