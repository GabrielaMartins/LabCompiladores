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
}
