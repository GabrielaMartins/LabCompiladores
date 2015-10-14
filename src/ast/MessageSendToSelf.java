/*
 * Gabriela de Jesus Martins	- 489689
 * Valdeir Soares Perozim		- 489786
 */

package ast;

public class MessageSendToSelf extends MessageSend {
	
	private KraClass thisClass;
	private KraClass objClass;
	private InstanceVariable var;
	private Method m;
	private ExprList realParams;
	
	public MessageSendToSelf(KraClass thisClass) {
		this.thisClass = thisClass;
		this.var = null;
		this.m = null;
		this.realParams = null;
	}
	
	public MessageSendToSelf(KraClass thisClass, InstanceVariable var) {
		this.thisClass = thisClass;
		this.var = var;
		this.m = null;
		this.realParams = null;
	}
    
	public MessageSendToSelf(KraClass thisClass, Method m, ExprList realParams) {
		this.thisClass = thisClass;
		this.var = null;
		this.m = m;
		this.realParams = realParams;
	}
    
    public void genKra(PW pw, boolean putParenthesis) {
    	
    }
    
    public void genC( PW pw, boolean putParenthesis ) {
    }   
    
    public Type getType() { 
        if (m != null) {
        	return m.getType();
        }
        
        if (var != null) {
        	return var.getType();
        }
        
        return new TypeIdent(thisClass.getName());
    }
}
