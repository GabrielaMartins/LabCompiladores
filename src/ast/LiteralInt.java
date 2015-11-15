/*
 * Gabriela de Jesus Martins	- 489689
 * Valdeir Soares Perozim		- 489786
 */

package ast;

public class LiteralInt extends Expr {
    
    public LiteralInt( int value ) { 
        this.value = value;
    }
    
    public int getValue() {
        return value;
    }
    public void genC( PW pw, boolean putParenthesis ) {
        pw.printIdent("" + value);
    }
    
    public void genKra ( PW pw, boolean putParenthesis ) {
        pw.print("" + value);
    }
    
    public Type getType() {
        return Type.intType;
    }
    
    private int value;
}
