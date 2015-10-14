package ast;

public class VariableExpr extends Expr {
    
    public VariableExpr( Variable v ) {
        this.v = v;
    }
    
    public void genC( PW pw, boolean putParenthesis ) {
        pw.print( v.getName() );
    }
    
    public void genKra(PW pw, boolean putParenthesis ){

	}
    
    public Type getType() {
        return v.getType();
    } 
    
    public Variable getV(){
    	return v;
    }
    
    private Variable v;
}