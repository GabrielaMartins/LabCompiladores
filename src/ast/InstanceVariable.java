/*
 * Gabriela de Jesus Martins	- 489689
 * Valdeir Soares Perozim		- 489786
 */

package ast;

public class InstanceVariable extends Variable {
	
	private boolean isStatic;

    public InstanceVariable( String name, Type type, boolean isStatic ) {
        super(name, type);
        this.isStatic = isStatic;
    }
    
    public boolean isStatic() {
    	return this.isStatic;
    }

}