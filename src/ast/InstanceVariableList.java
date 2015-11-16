/*
 * Gabriela de Jesus Martins	- 489689
 * Valdeir Soares Perozim		- 489786
 */

package ast;

import java.util.*;

public class InstanceVariableList {

    public InstanceVariableList() {
       instanceVariableList = new ArrayList<InstanceVariable>();
    }
    
    public void genC(PW pw){
    	Iterator <InstanceVariable> it = instanceVariableList.iterator();
    	boolean firstIt = true;
    	
    	while(it.hasNext()){
    		InstanceVariable iv = it.next();
    		
    		if(firstIt == true){
    			pw.printIdent(iv.getType().getCname() + " ");
    		} 
    		
    		iv.genKra(pw);
    		
    		if(it.hasNext()){
    			pw.print(", ");
    		}else{
    			pw.print(";");
    		}
    		
    	}
    	
    	pw.println("");
    }
    
    public void genKra(PW pw) {
    	Iterator <InstanceVariable> it = instanceVariableList.iterator();
    	boolean firstIt = true;
    	
    	while(it.hasNext()){
    		InstanceVariable iv = it.next();
    		
    		if(firstIt == true){
    			if(iv.isStatic()==true){
    				pw.printIdent("static ");
    				pw.print("private ");
    			}else{
        			pw.printIdent("private ");
    			}
    			
    			pw.print(iv.getType().getName() + " ");
    		} 
    		
    		iv.genKra(pw);
    		
    		if(it.hasNext()){
    			pw.print(", ");
    		}else{
    			pw.println(";");
    		}
    		
    	}
    	
    	pw.println("");
    }

    public void addElement(InstanceVariable instanceVariable) {
       instanceVariableList.add( instanceVariable );
    }

    public Iterator<InstanceVariable> elements() {
    	return this.instanceVariableList.iterator();
    }

    public int getSize() {
        return instanceVariableList.size();
    }

    private ArrayList<InstanceVariable> instanceVariableList;

}
