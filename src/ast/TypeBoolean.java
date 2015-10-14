/*
 * Gabriela de Jesus Martins	- 489689
 * Valdeir Soares Perozim		- 489786
 */

package ast;

public class TypeBoolean extends Type {

   public TypeBoolean() { super("boolean"); }

   @Override
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
