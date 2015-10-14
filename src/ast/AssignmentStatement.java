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
	
	public void genKra(PW pw){
		pw.add();
		if(this.left != null){
			this.left.genKra(pw, false);
		}
		if(this.right!= null){
			pw.print(this.getType().toString());
			this.right.genKra(pw, false);
		}
		if(this.left != null || this.right != null)
			pw.println(";");
	}
	
	public StatementType getType(){
		return type;
	}

}
