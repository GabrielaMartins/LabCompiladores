package ast;

public class MessageSendStatement extends Statement { 
	
	private MessageSend  messageSend;

	public void genC( PW pw ) {
		pw.printIdent("");
		// messageSend.genC(pw);
		pw.println(";");
	}
	
	public void genKra( PW pw) {
        
    }
	
	@Override
	public StatementType getType() {
		return null;
	}
}
