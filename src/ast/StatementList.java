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
	
    private ArrayList<Statement> statementList;

    public StatementList() {
    	statementList = new ArrayList<Statement>();
    }
    
    public void genKra(PW pw){
    	
    }

    public void addElement(Statement statement) {
    	statementList.add( statement );
    }
    
    public Statement getElement(StatementType type) {
    	
    	Iterator<Statement> it = elements();
    	while (it.hasNext()) {
    		Statement s = (Statement) it.next();
    		if (s != null && s.getType() == type) {
    			return s;
    		}
    	}
    	
    	return null;
    }

    public Iterator<Statement> elements() {
    	return this.statementList.iterator();
    }

    public int getSize() {
        return statementList.size();
    }
}
