package ast;

public class NewExpr extends Expr {
	
	Type type;
	
	public NewExpr(Type type){
		this.type = type;
	}
	
	public Type getType(){
		return this.type;
	}
	
	public void genC(PW pw, boolean putParenthesis ){
		
	}
	
	public void genKra(PW pw, boolean putParenthesis ){

	}
}
