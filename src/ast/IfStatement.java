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
	}
	
	public void genKra(PW pw){

	}
}
