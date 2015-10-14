package ast;

public class Variable {

    public Variable( String name, Type type ) {
        this.name = name;
        this.type = type;
        this.isNull = false;
    }
    
    public void genKra(PW pw) {
    	pw.print(getName());
    }

    public String getName() { 
    	return name; 
    }

    public Type getType() {
        return type;
    }
    //Gabriela
    public void setIsNull(boolean isNull){
    	this.isNull = isNull;
    }
    
    public boolean getIsNull(){
    	return this.isNull;
    }
    //$Gabriela

    private String name;
    private Type type;
    //Gabriela
    private boolean isNull;
    //$Gabriela
}