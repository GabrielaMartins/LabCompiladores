/*
 * Universidade Federal de São Carlos - Campus Sorocaba
 * Laboratório de Compiladores 2015/2
 * 
 * Gabriela de Jesus Martins	- 489689
 * Valdeir Soares Perozim		- 489786
 */

package ast;

import lexer.Symbol;

public class Method {

    public Method( String name, Type type, Symbol qualifier ) {
        this.name = name;
        this.type = type;
        this.qualifier = qualifier;
        this.finalQualifier = null;
        this.staticQualifier = null;
    }

    public String getName() { 
    	return name; 
    }

    public Type getType() {
        return type;
    }
    
    public Symbol getQualifier() {
    	return qualifier;
    }
    
    public Symbol getFinal() {
    	return finalQualifier;
    }
    
    public Symbol getStatic() {
    	return staticQualifier;
    }
    
    public void setFinal() {
    	finalQualifier = Symbol.FINAL;
    }
    
    public void setStatic() {
    	staticQualifier = Symbol.STATIC;
    }

    private String name;
    private Type type;
    private Symbol qualifier;
    private Symbol finalQualifier;
    private Symbol staticQualifier;
}
