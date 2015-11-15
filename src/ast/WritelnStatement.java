/*
 * Gabriela de Jesus Martins	- 489689
 * Valdeir Soares Perozim		- 489786
 */

package ast;

public class WritelnStatement extends Statement {
	
	ExprList exprlist;
	StatementType type;
	
	public WritelnStatement(ExprList exprlist){
		this.exprlist = exprlist;
		this.type = StatementType.Writeln;
	}
	
	@Override
	public StatementType getType() {
		return type;
	}
	
	@Override
	public void genC(PW pw) {
	}
	
	public void genKra(PW pw){
		pw.printIdent("writeln(\"");
		this.exprlist.genKra(pw);
		pw.println("\");");
	}
}