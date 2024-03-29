/*
 * Gabriela de Jesus Martins	- 489689
 * Valdeir Soares Perozim		- 489786
 */

package ast;

public class IfStatement extends Statement {
	
	StatementType type;
	Expr e;
	StatementList s;
	
	public IfStatement(Expr e, StatementList s){
		this.e = e;
		this.s = s;
		this.type = StatementType.If;
	}
	
	@Override
	public StatementType getType() {
		return type;
	}
	
	@Override
	public void genC(PW pw) {
		pw.printIdent("if");
		e.genKra(pw, true);
		pw.println("{");
		pw.add();
		s.genKra(pw);
		pw.sub();
		pw.println("}");
		pw.println("");
	}
	
	public void genKra(PW pw){
		pw.printIdent("if");
		e.genKra(pw, true);
		pw.println("{");
		pw.add();
		s.genKra(pw);
		pw.sub();
		pw.println("}");
		pw.println("");
	}
}
