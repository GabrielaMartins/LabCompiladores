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
		if(this.left != null){
			if(left instanceof VariableExpr){
				VariableExpr v = (VariableExpr)left;
				pw.printIdent(v.getV().getName());
			}else{
				this.left.genKra(pw, false);
			}
			
			if(this.right!= null){
				pw.print(this.getType().toString());
				this.right.genKra(pw, false);
			}
				
			pw.println(";");
		}
	}
	
	public void genKra(PW pw){
		if(this.left != null){
			if(left instanceof VariableExpr){
				VariableExpr v = (VariableExpr)left;
				pw.printIdent(v.getV().getName());
			}else{
				this.left.genKra(pw, false);
			}
			
			if(this.right!= null){
				pw.print(" " + this.getType().toString() + " ");
				this.right.genKra(pw, false);
			}
				
			pw.println(";");
		}	
	}
	
	public StatementType getType(){
		return type;
	}

}
