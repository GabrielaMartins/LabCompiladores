/*
 * Gabriela de Jesus Martins	- 489689
 * Valdeir Soares Perozim		- 489786
 */

package ast;

import java.util.*;

public class ParameterList {

    public ParameterList() {
       paramList = new ArrayList<Parameter>();
    }
    
    public void genKra(PW pw){
    	Iterator<Parameter> it = elements();
    	
    	while(it.hasNext()){
    		it.next().genKra(pw);
    		
    		if(it.hasNext()){
    			pw.print(", ");
    		}
    	}
    	
    }

    public void addElement(Parameter p) {
       paramList.add(p);
    }

    public Iterator<Parameter> elements() {
        return paramList.iterator();
    }

    public int getSize() {
        return paramList.size();
    }

    private ArrayList<Parameter> paramList;
}
