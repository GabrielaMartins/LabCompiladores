/*
 * Universidade Federal de S�o Carlos - Campus Sorocaba
 * Laborat�rio de Compiladores 2015/2
 * 
 * Gabriela de Jesus Martins	- 489689
 * Valdeir Soares Perozim		- 489786
 */

package ast;

import java.util.*;

public class MethodList {

    public MethodList() {
       methodList = new ArrayList<Method>();
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

    private ArrayList<Method> methodList;

}