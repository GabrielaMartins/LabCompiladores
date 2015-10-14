/*
 * Universidade Federal de São Carlos - Campus Sorocaba
 * Laboratório de Compiladores 2015/2
 * 
 * Gabriela de Jesus Martins	- 489689
 * Valdeir Soares Perozim		- 489786
 */

package ast;

import java.util.*;

public class MethodList {
	
    private ArrayList<Method> methodList;

    public MethodList() {
       methodList = new ArrayList<Method>();
    }
    
    public void genKra(PW pw) {
    	
    	Iterator<Method> it = elements();
    	while (it.hasNext()) {
    		it.next().genKra(pw);
    	}
    }

    public void addElement(Method method) {
       methodList.add( method );
    }

    public Iterator<Method> elements() {
    	return this.methodList.iterator();
    }

    public int getSize() {
        return methodList.size();
    }
}
