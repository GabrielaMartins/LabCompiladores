/*
 * Gabriela de Jesus Martins	- 489689
 * Valdeir Soares Perozim		- 489786
 */

package ast;

public class MessageSendStatement extends Statement { 
	
	private MessageSend  messageSend;

	public void genC( PW pw ) {
		pw.printIdent("");
		// messageSend.genC(pw);
		pw.println(";");
	}
	
	@Override
	public StatementType getType() {
		return null;
	}
}
