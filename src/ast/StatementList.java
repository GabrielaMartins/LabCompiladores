/*
 * Universidade Federal de São Carlos - Campus Sorocaba
 * Laboratório de Compiladores 2015/2
 * 
 * Gabriela de Jesus Martins	- 489689
 * Valdeir Soares Perozim		- 489786
 */

package ast;

import java.util.*;

public class StatementList {

    public StatementList() {
    	statementList = new ArrayList<Statement>();
    }

    public void addElement(Statement statement) {
    	statementList.add( statement );
    }

    public Iterator<Statement> elements() {
    	return this.statementList.iterator();
    }

    public int getSize() {
        return statementList.size();
    }

    private ArrayList<Statement> statementList;

}
