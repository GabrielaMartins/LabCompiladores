/*
 * Gabriela de Jesus Martins	- 489689
 * Valdeir Soares Perozim		- 489786
 */

package ast;

import lexer.*;

public class SignalExpr extends Expr {

    public SignalExpr( Symbol oper, Expr expr ) {
       this.oper = oper;
       this.expr = expr;
    }

    @Override
	public void genC( PW pw, boolean putParenthesis ) {
       if ( putParenthesis )
          pw.print("(");
       pw.print( oper == Symbol.PLUS ? "+" : "-" );
       expr.genC(pw, true);
       if ( putParenthesis )
          pw.print(")");
    }
    
    public void genKra(PW pw, boolean putParenthesis ){
    	if ( putParenthesis )
            pw.print("(");
         pw.print( oper == Symbol.PLUS ? "+" : "-" );
         expr.genC(pw, true);
         if ( putParenthesis )
            pw.print(")");
	}

    @Override
	public Type getType() {
       return expr.getType();
    }

    private Expr expr;
    private Symbol oper;
}
