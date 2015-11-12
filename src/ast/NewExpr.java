/*
 * Gabriela de Jesus Martins	- 489689
 * Valdeir Soares Perozim		- 489786
 */
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
		pw.print("new " + this.type.getName() + "()");
	}
}
