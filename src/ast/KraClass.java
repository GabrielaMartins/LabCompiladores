package ast;
/*
 * Krakatoa Class
 */
public class KraClass extends Type {
	
   public KraClass( String name ) {
      super(name);
   }
   
   public String getCname() {
      return getName();
   }
   
   private String name;
   private KraClass superclass;
   private InstanceVariableList instanceVariableList;
   private MethodList publicMethodList, privateMethodList;
   // m�todos p�blicos get e set para obter e iniciar as vari�veis acima,
   // entre outros m�todos
   
   public void setPrivateMethodList(MethodList privateMethodList) {
	   this.privateMethodList = privateMethodList;
   }

   public void setPublicMethodList(MethodList publicMethodList) {
	   this.publicMethodList = publicMethodList;
   }

   public MethodList getPrivateMethodList() {
	   return privateMethodList;
   }

   public MethodList getPublicMethodList() {
	   return publicMethodList;
   }
}
