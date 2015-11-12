/*
 * Universidade Federal de São Carlos - Campus Sorocaba
 * Laboratório de Compiladores 2015/2
 * 
 * Gabriela de Jesus Martins	- 489689
 * Valdeir Soares Perozim		- 489786
 */

package ast;

import java.util.ArrayList;
import java.util.Iterator;
import lexer.Symbol;

public class Method implements Comparable<Method> {
	
    private String name;
    private Type type;
    private Symbol qualifier; //private or public?
    private Symbol finalQualifier;
    private Symbol staticQualifier;
    private ParameterList paramList;
    private StatementList statements;
    private ArrayList<LocalVariableList> variableList;

    public Method( String name, Type type, Symbol qualifier ) {
        this.name = name;
        this.type = type;
        this.qualifier = qualifier;
        this.finalQualifier = null;
        this.staticQualifier = null;
        this.paramList = new ParameterList();
        this.statements = new StatementList();
        this.variableList = new ArrayList<>();
    }
    
    public void genKra(PW pw){
    	pw.add();
    	boolean first = true;
    	if(this.finalQualifier != null){
    		pw.printIdent(this.finalQualifier.toString() + " ");
    		if(this.staticQualifier != null){
    			pw.print(this.staticQualifier.toString()+ " ");
    		}
    		pw.print(this.qualifier.toString()+ " ");
    	}else if(this.staticQualifier != null){
    		pw.printIdent(this.staticQualifier.toString() + " ");
    		pw.print(this.qualifier.toString()+ " ");
    	}else{
    		pw.printIdent(this.qualifier.toString()+ " ");
    	}
    	   	
    	pw.print(this.getName() + "(");
    	this.paramList.genKra(pw);
    	pw.println("){");
    	Iterator<LocalVariableList> it = variableList.iterator();
    	pw.add();
    	while(it.hasNext()){
    		Iterator<Variable> it2 = it.next().elements();
    		while (it2.hasNext()){
    			Variable v = it2.next();
    			if(first == true){
    				pw.printIdent(v.getType().getName() + " ");
    				first = false;
    			}
    			
    			v.genKra(pw);
    			
    			if(it2.hasNext()){
    				pw.print(", ");
    			}
    		}
    		
    		first = true;
    		pw.println(";");
    	}
    	pw.println("");
    	this.statements.genKra(pw);
    	pw.sub();
    	pw.printIdent("}");
    	pw.println("");
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
    
    public ParameterList getParamList() {
    	return paramList;
    }
    
    public StatementList getStatementList() {
    	return statements;
    }
    
    public boolean isPrivate() {
    	return qualifier == Symbol.PRIVATE;
    }
    
    public boolean isPublic() {
    	return qualifier == Symbol.PUBLIC;
    }
    
    public boolean isFinal() {
    	return finalQualifier != null;
    }
    
    public boolean isStatic() {
    	return staticQualifier != null;
    }
    
    public void setFinal() {
    	finalQualifier = Symbol.FINAL;
    }
    
    public void setStatic() {
    	staticQualifier = Symbol.STATIC;
    }
    
    public void setParamList(ParameterList paramList) {
    	this.paramList = paramList;
    }
    
    public void setStatementList(StatementList statements) {
    	this.statements = statements;
    }
    
    public void addVariableList(LocalVariableList vl) {
    	this.variableList.add(vl);
    }
    
    @Override
	public int compareTo(Method other) {
		int thisParamSize, otherParamSize;
    	
    	if (! this.name.equals(other.getName())) {	
    		return this.name.compareTo(other.getName());
    	}
    	
    	if (this.type != other.getType()) {
    		return this.type.getName().compareTo(other.getType().getName());
    	}
    	
    	thisParamSize = this.getParamList().getSize();
    	otherParamSize = other.getParamList().getSize();
    	if (thisParamSize != otherParamSize ) {
    		return Integer.compare(thisParamSize, otherParamSize);
    	}
    	
    	Iterator<Parameter> thisIt, otherIt;
    	thisIt = this.getParamList().elements();
    	otherIt = other.getParamList().elements();
    	
    	while(thisIt.hasNext() && otherIt.hasNext()) {
    		
    		Parameter thisParam = (Parameter) thisIt.next();
    		Parameter otherParam = (Parameter) otherIt.next();
    		if (thisParam.getType() != otherParam.getType()) {
    			return thisParam.getType().getName().compareTo(otherParam.getType().getName());
    		}
    		thisParamSize--;
    		otherParamSize--;
    	}
    	
    	return Integer.compare(thisParamSize, otherParamSize);
	}
}
