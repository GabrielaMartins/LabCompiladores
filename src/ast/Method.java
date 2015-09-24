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
        this.paramList = null;
        this.statements = null;
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
    
    public ParamList getParamList() {
    	return paramList;
    }
    
    public StatementList getStatementList() {
    	return statements;
    }
    
    public Symbol isFinal() {
    	return finalQualifier;
    }
    
    public Symbol isStatic() {
    	return staticQualifier;
    }
    
    public void setFinal() {
    	finalQualifier = Symbol.FINAL;
    }
    
    public void setStatic() {
    	staticQualifier = Symbol.STATIC;
    }
    
    public void setParamList(ParamList paramList) {
    	this.paramList = paramList;
    }
    
    public void setStatementList(StatementList statements) {
    	this.statements = statements;
    }

    private String name;
    private Type type;
    private Symbol qualifier; //private or public?
    private Symbol finalQualifier;
    private Symbol staticQualifier;
    private ParamList paramList;
    private StatementList statements;
}
