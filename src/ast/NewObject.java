/*
 * Gabriela de Jesus Martins	- 489689
 * Valdeir Soares Perozim		- 489786
 */

package ast;

public class NewObject extends Expr {
	
	KraClass newClass;
	
	public NewObject(KraClass newClass){
		this.newClass = newClass;
	}
	
	public void genKra(PW pw, boolean putParenthesis) {
		
	}
	
	public void genC(PW pw, boolean putParenthesis ){
		
	}
	
	public Type getType(){
		return new TypeIdent(newClass.getName());
	}
}
