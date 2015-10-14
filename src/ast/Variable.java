/*
 * Gabriela de Jesus Martins	- 489689
 * Valdeir Soares Perozim		- 489786
 */
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

    public void setIsNull(boolean isNull){
    	this.isNull = isNull;
    }
    
    public boolean getIsNull(){
    	return this.isNull;
    }

    private String name;
    private Type type;

    private boolean isNull;
}