package ast;

public class ReturnStatement extends Statement {
	
	StatementType type;
	Expr expr;
	
	public ReturnStatement(Expr expr) {
		this.type = StatementType.Return;
		this.expr = expr;
	}
	
	@Override
	public StatementType getType() {
		return type;
	}
	
	@Override
	public void genC(PW pw) {
	}

}
