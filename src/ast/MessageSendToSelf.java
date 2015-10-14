/*
 * Gabriela de Jesus Martins	- 489689
 * Valdeir Soares Perozim		- 489786
 */

package ast;

public class MessageSendToSelf extends MessageSend {
	
	private Method m;
	private ExprList realParams;
    
    public Type getType() { 
        return m.getType();
    }
    
    public void genC( PW pw, boolean putParenthesis ) {
    }    
}
