package ast;

public class NewExpr extends Expr {
	Type type;
	ExprList exprlist;
	
	public NewExpr(Type type, ExprList exprlist){
		this.type = type;
		this.exprlist = exprlist;
	}
	
	public Type getType(){
		return this.type;
	}
	
	public void genC(PW pw, boolean putParenthesis ){
		
	}
}
