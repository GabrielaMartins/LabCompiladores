/*
 * Gabriela de Jesus Martins	- 489689
 * Valdeir Soares Perozim		- 489786
 */

package ast;

public class TypeIdent extends Type {
	
	public TypeIdent( String name) { 
		super(name);
	}
	
	@Override
	public String getCname() {
		return getName();
	}
	
	@Override
	public int compareTo(Type o) {
		if (o == null) {
			return -1;
		}
		
		return this.getName().compareTo(o.getName());
	}
}
