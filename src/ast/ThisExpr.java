package ast;

public class ThisExpr extends Expr{
	Type type;
	ExprList exprlist;
	
	public ThisExpr(Type type, ExprList exprlist){
		this.type = type;
		this.exprlist = exprlist;
	}
	
	public void genC(PW pw, boolean putParenthesis){
		
	}
	
	public Type getType(){
		return type;
	}
	
	
}
