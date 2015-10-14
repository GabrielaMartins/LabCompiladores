 /* 
 * Gabriela de Jesus Martins	- 489689
 * Valdeir Soares Perozim		- 489786
 */

package ast;


public class Parameter extends Variable {

    public Parameter( String name, Type type ) {
        super(name, type);
    }
    
    public void genKra(PW pw){
    	
    	pw.print(this.getType().getName() + " ");
    	pw.print(this.getName());
    }

}