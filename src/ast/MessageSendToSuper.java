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
	
	public void genKra(PW pw, boolean putParenthesis) {
		pw.add();
		pw.printIdent("super.");
		pw.sub();
		//realParams.genKra(pw, true);
		pw.add();
		pw.printlnIdent(";");
	}

    public void genC( PW pw, boolean putParenthesis ) {
        
    }
    
    public Type getType() { 
        return m.getType();
    }    
}
