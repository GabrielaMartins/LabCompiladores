package ast;

/*
 * Krakatoa Class
 */

import java.util.Iterator;
import lexer.Symbol;

public class KraClass extends Type {
	
   public KraClass( String name, Symbol qualifier, KraClass superClass ) {
      super(name);
      this.qualifier = qualifier;
      this.superclass = superClass;
      this.instanceVariableList = null;
      this.publicMethodList = new MethodList();
      this.privateMethodList = new MethodList();
   }
   
   public String getCname() {
      return getName();
   }
   
   public Symbol getQualifier() {
	   return qualifier;
   }
   
   public void setInstanceVariableList(InstanceVariableList ivl) {
	   this.instanceVariableList = ivl;
   }
   
   public void addPublicMethod(Method method) {
	   
	   publicMethodList.addElement(method);
   }
   
   public void addPrivateMethod(Method method) {
	   
	   privateMethodList.addElement(method); 
   }
   
   public Method searchMethod(Method method) {
	   
	   Iterator<Method> it = publicMethodList.elements();
	   while(it.hasNext()) {
		   
		   Method thisMethod = (Method) it.next();
		   if (thisMethod == method) {
			   return thisMethod;
		   }
	   }
	   
	   it = privateMethodList.elements();
	   while(it.hasNext()) {
		   
		   Method thisMethod = (Method) it.next();
		   if (thisMethod == method) {
			   return thisMethod;
		   }
	   }
	   
	   return null;
   }
   
   private String name;
   private KraClass superclass;
   private Symbol qualifier;
   private InstanceVariableList instanceVariableList;
   private MethodList publicMethodList, privateMethodList;
}
