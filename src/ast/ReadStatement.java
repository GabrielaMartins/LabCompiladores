/*
 * Gabriela de Jesus Martins	- 489689
 * Valdeir Soares Perozim		- 489786
 */

package ast;

public class ReadStatement extends Statement{
	
	StatementType type;
	Variable var;
	
	public ReadStatement(Variable var){
		this.var = var;
		this.type = StatementType.Read;
	}
	
	@Override
	public StatementType getType() {
		return type;
	}
	
	@Override
	public void genC(PW pw) {
	}
	
	public void genKra(PW pw){
		pw.println("");
		pw.printIdent("read(");
		this.var.genKra(pw);
		pw.println(");");
	}
}
