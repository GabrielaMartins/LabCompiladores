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

	}
}
