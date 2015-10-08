package ast;

public class TypeIdent extends Type {
	public TypeIdent( String name) { super(name);}

	   @Override
	   public String getCname() {
	      return getName();
	   }
}
