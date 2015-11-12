package ast;

/*
 * Gabriela de Jesus Martins	- 489689
 * Valdeir Soares Perozim		- 489786
 */
public class VariableExpr extends Expr {
    
    public VariableExpr( Variable v ) {
        this.v = v;
    }
    
    public void genC( PW pw, boolean putParenthesis ) {
       // pw.print( v.getName() );
    }
    
    public void genKra(PW pw, boolean putParenthesis ){
    	if(putParenthesis){
    		pw.print("(");
        	pw.print(v.getName());
        	pw.print(")");
    	}else{
    		pw.print(v.getName());
    	}
    		
	}
    
    public Type getType() {
        return v.getType();
    } 
    
    public Variable getV(){
    	return v;
    }
    
    private Variable v;
}