/*
 * Gabriela de Jesus Martins	- 489689
 * Valdeir Soares Perozim		- 489786
 */

package ast;

public class WriteStatement extends Statement {
	
	ExprList exprlist;
	StatementType type;
	
	public WriteStatement(ExprList exprlist){
		this.exprlist = exprlist;
		this.type = StatementType.Write;
	}
	
	@Override
	public StatementType getType() {
		return type;
	}
	
	@Override
	public void genC(PW pw) {
	}
	
	public void genKra(PW pw){
		pw.printIdent("write(\"");
		this.exprlist.genKra(pw);
		pw.println("\");");
	}
}
