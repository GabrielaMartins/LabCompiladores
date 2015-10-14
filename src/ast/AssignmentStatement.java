/*
 * Gabriela de Jesus Martins	- 489689
 * Valdeir Soares Perozim		- 489786
 */

package ast;

public class AssignmentStatement extends Statement {
	
	StatementType type;
	Expr left;
	Expr right;
	
	public AssignmentStatement(Expr left, Expr right){
		this.left = left;
		this.right = right;
		this.type = StatementType.Assignment;
	}
	
	public void genC(PW pw){
		
	}
	
	public StatementType getType(){
		return type;
	}

}
