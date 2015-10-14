 /* 
 * Gabriela de Jesus Martins	- 489689
 * Valdeir Soares Perozim		- 489786
 */

package ast;

import java.util.*;

public class LocalVariableList {
	
    private ArrayList<Variable> localList;

    public LocalVariableList() {
       localList = new ArrayList<Variable>();
    }

    public void addElement(Variable v) {
       localList.add(v);
    }

    public Iterator<Variable> elements() {
        return localList.iterator();
    }

    public int getSize() {
        return localList.size();
    }
}
