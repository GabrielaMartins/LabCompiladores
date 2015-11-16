/*
 * Gabriela de Jesus Martins	- 489689
 * Valdeir Soares Perozim		- 489786
 */

package ast;

import java.util.*;

public class KraClassList {
	
    private ArrayList<KraClass> classList;

    public KraClassList() {
       classList = new ArrayList<KraClass>();
    }

    public void addElement(KraClass kraClass) {
       classList.add( kraClass );
    }

    public Iterator<KraClass> elements() {
    	return this.classList.iterator();
    }
    
    public KraClass getElement(String name) {
    	
    	Iterator<KraClass> it;
    	it = classList.iterator();
    	
    	while(it.hasNext()) {
    		KraClass k = (KraClass) it.next();
    		if (k.getName().equals(name)) {
    			return k;
    		}
    	}
    	
    	return null;
    }

    public int getSize() {
        return classList.size();
    }
    
    public void genKra(PW pw) {
    	
    	Iterator<KraClass> it = elements();
    	while (it.hasNext()) {
    		
    		it.next().genKra(pw);
    	}
    }
    
    public void genC(PW pw){
    	Iterator<KraClass> it = elements();
    	while (it.hasNext()) {
    		
    		it.next().genC(pw);
    	}
    }
}
