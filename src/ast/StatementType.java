package ast;

public enum StatementType {
	
	Return("Return"),
	Assignment("="),
	Write("Write"),
	Read("Read"),
	While("While"),
	If("If"),
	Composite("Composite");
	
	private String name;
	
	StatementType(String name) {
		this.name = name;
	}

	@Override public String toString() {
		return name;
	}	
}
