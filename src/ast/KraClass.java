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
      this.superClass = superClass;
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
   
   public boolean hasSuper() {
	   return this.superClass != null;
   }
   
   public Method searchMethod(Method method) {
	   
	   Iterator<Method> it = publicMethodList.elements();
	   while(it.hasNext()) {
		   
		   Method thisMethod = (Method) it.next();
		   if (thisMethod.compareTo(method) == 0) {
			   return thisMethod;
		   }
	   }
	   
	   it = privateMethodList.elements();
	   while(it.hasNext()) {
		   
		   Method thisMethod = (Method) it.next();
		   if (thisMethod.compareTo(method) == 0) {
			   return thisMethod;
		   }
	   }
	   
	   return null;
   }
   
   public Method searchMethodS(Method method) {
	   
	   if (superClass != null) {
		   return superClass.searchMethod(method);
	   }
	   
	   return null;
   }
   
   public void printPublic() {
	   printM(publicMethodList);
   }
   
   public void printPrivate() {
	   printM(privateMethodList);
   }
   
   public void printM(MethodList ml) {
	   Iterator<Method> it = ml.elements();
	   while(it.hasNext()) {
		   System.out.println();
		   Method thisMethod = (Method) it.next();
		   if (thisMethod.isFinal()) {
			   System.out.print("final ");
		   }
		   if (thisMethod.isStatic()) {
			   System.out.print("static ");
		   }
		   System.out.print(thisMethod.getQualifier() + " " + thisMethod.getType().getName() + " " + thisMethod.getName() + "(");
		   
		   ParameterList param = thisMethod.getParamList();
		   Iterator<Parameter> p = param.elements();
		   while (p.hasNext()) {
			   Parameter pm = (Parameter) p.next();
			   System.out.print(pm.getType().getName() + " " + pm.getName() + " ");
		   }
		   
		   System.out.print(");");
	   }
	   System.out.println();
   }
   
   private String name;
   private KraClass superClass;
   private Symbol qualifier;
   private InstanceVariableList instanceVariableList;
   private MethodList publicMethodList, privateMethodList;
}
