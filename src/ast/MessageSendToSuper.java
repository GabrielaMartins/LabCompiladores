/*
 * Gabriela de Jesus Martins	- 489689
 * Valdeir Soares Perozim		- 489786
 */

package ast;

public class MessageSendToSuper extends MessageSend {
	
	private Method m;
	private ExprList realParams;
	
	public MessageSendToSuper(Method method, ExprList params) {
		this.m = method;
		this.realParams = params;
	}

    public Type getType() { 
        return m.getType();
    }

    public void genC( PW pw, boolean putParenthesis ) {
        
    }
    
    public void genKra( PW pw, boolean putParenthesis ) {
    }
    
}