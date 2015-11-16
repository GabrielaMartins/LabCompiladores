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
    	pw.println("");
    	pw.printIdent("super.");
    	pw.print(m.getName()+ "(");
    	//realParams.genKra(pw);
    	pw.println(")");
	}

    public void genC( PW pw, boolean putParenthesis ) {
        
    }

    public Type getType() { 
        return m.getType();
    }    
}
