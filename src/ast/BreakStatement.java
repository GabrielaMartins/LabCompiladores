/*
 * Gabriela de Jesus Martins	- 489689
 * Valdeir Soares Perozim		- 489786
 */

package ast;

public class BreakStatement extends Statement { 
	
	public void genC( PW pw ) {
		pw.println("break;");
	}
	
	public void genKra( PW pw) {
        pw.println("break;");
    }
	
	@Override
	public StatementType getType() {
		return StatementType.Break;
	}
}
