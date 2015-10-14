package ast;

public class WhileStatement extends Statement {
	
	StatementType type;
	Expr expr;
	Statement s;
	
	public WhileStatement(Expr expr, Statement s){
		this.expr = expr;
		this.s = s;
		this.type = StatementType.While;
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
