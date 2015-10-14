/*
 * Gabriela de Jesus Martins	- 489689
 * Valdeir Soares Perozim		- 489786
 */

package ast;


public class MessageSendToVariable extends MessageSend { 
	
	private KraClass classe;
	private Method m;
	private ExprList realParams;
	private InstanceVariable staticVar;
	private Variable var;
	
	public MessageSendToVariable(Variable var, Method m, ExprList realParams) {
		this.var = var;
		this.m = m;
		this.realParams = realParams;
	}
	
	public void genKra(PW pw, boolean putParenthesis) {
		
	}
    
    public void genC( PW pw, boolean putParenthesis ) {
        
    }
    
    public Type getType() { 
        return m.getType();
    }
}
