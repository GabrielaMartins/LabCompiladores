package ast;

public enum StatementType {
	
	Return("Return");
	
	private String name;
	
	StatementType(String name) {
		this.name = name;
	}

	@Override public String toString() {
		return name;
	}	
}
