/*
 * Gabriela de Jesus Martins	- 489689
 * Valdeir Soares Perozim		- 489786
 */

package ast;

public class CompositeStatement extends Statement{
	
	StatementType type;
	StatementList s;
	
	public CompositeStatement(StatementList s){
		this.s = s;
		this.type = StatementType.Composite;
	}
	
	@Override
	public StatementType getType() {
		return type;
	}
	
	@Override
	public void genC(PW pw) {
	}
	
	public void genKra(PW pw){
		s.genKra(pw);
	}
}
