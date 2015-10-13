package ast;

public class WritelnStatement extends Statement {
	
	ExprList exprlist;
	StatementType type;
	
	public WritelnStatement(ExprList exprlist){
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
}