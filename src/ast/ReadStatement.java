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
}
