/*
 * Gabriela de Jesus Martins	- 489689
 * Valdeir Soares Perozim		- 489786
 */

package ast;

/*
 * Krakatoa Class
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import lexer.Symbol;


public class KraClass extends Type {
	
	private String name;
	private KraClass superClass;
	private Symbol qualifier;
	private ArrayList<InstanceVariableList> instanceVariableList;
	private MethodList publicMethodList, privateMethodList;
    private HashMap<String, Variable> localTable;
	
	public KraClass( String name, Symbol qualifier, KraClass superClass ) {
		super(name);
		this.qualifier = qualifier;
		this.superClass = superClass;
		this.instanceVariableList = new ArrayList<>();
		this.publicMethodList = new MethodList();
		this.privateMethodList = new MethodList();
        this.localTable  = new HashMap<String, Variable>();
	}
	
    public void genKra(PW pw) {
    	
    	if (qualifier != null) {
    		pw.print(qualifier.name() + " ");
    	}
    	pw.print("class " + this.getName() + " ");
    	
    	if (superClass != null) {
    		pw.print("extends " + superClass.getName() + " ");
    	}
    	pw.println("{");
    	
    	Iterator<InstanceVariableList> it = instanceVariableList.iterator();
    	while (it.hasNext()) {
    		it.next().genKra(pw);
    	}
    	
    	this.privateMethodList.genKra(pw);
    	this.publicMethodList.genKra(pw);
    	
    	pw.println("}");
    	pw.println("");
    }

	public String getCname() {
		return getName();
	}
	
	public void setSuper(KraClass superClass) {
		this.superClass = superClass;
	}

	public KraClass getSuper() {
		return superClass;
	}
	
	public void setFinal() {
		this.qualifier = Symbol.FINAL;
	}
	public boolean isFinal() {
		return qualifier != null;
	}

	public void addVariableList(InstanceVariableList ivl) {
		this.instanceVariableList.add(ivl);
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

	/**
	 * Verifica se é possível chamar o método passado como parâmetro
	 * na interface pública
	 * @return Methodo encontrada
	 * @param Nome do método para buscar
	 */
	public Method callMethod(String name) {
		Method m;

		m = searchInPublic(name, false);
		if (m != null) {
			return m;
		}
		
		if (superClass == null) {
			return null;
		}

		return superClass.callMethod(name);

	}
	
	public Method callStaticMethod(String name) {
		
		Method m;
		m = searchInPublic(name, true);
		if (m != null) {
			return m;
		} else {
			m = searchInPrivate(name, true);
			if (m != null) {
				return m;
			}
		}
		
		return null;
	}

	private Method searchInPublic(String name, boolean isStatic) {

		Iterator<Method> it = publicMethodList.elements();
		while(it.hasNext()) {

			Method thisMethod = (Method) it.next();
			if (thisMethod.getName().equals(name)) {
				if (thisMethod.isStatic() && isStatic)
					return thisMethod;
				else if (thisMethod.isStatic() == false && isStatic == false)
					return thisMethod;
			}
		}

		return null;
	}

	private Method searchInPrivate(String name, boolean isStatic) {

		Iterator<Method> it = privateMethodList.elements();
		while(it.hasNext()) {

			Method thisMethod = (Method) it.next();
			if (thisMethod.getName().equals(name)) {
				if (thisMethod.isStatic() && isStatic)
					return thisMethod;
				else if (thisMethod.isStatic() == false && isStatic == false)
					return thisMethod;
			}
		}

		return null;
	}

	public Method searchMethod(String name) {

		Method m;
		if ( (m = searchInPublic(name, false)) != null ) {
			return m;
		}

		return searchInPrivate(name, false);
	}
	
	public Method searchStaticMethod(String name) {

		Method m;
		if ( (m = searchInPublic(name, true)) != null ) {
			return m;
		}

		return searchInPrivate(name, true);
	}

	public Method searchMethodS(String name) {

		if (superClass != null) {
			
			Method m = superClass.searchMethod(name);
			if (m == null)
				return superClass.searchMethodS(name);
			else
				return m;
		}

		return null;
	}
	
    public Variable putInLocal(String key, Variable value) {
    	return localTable.put(key, value);
    }
    
    public Variable getInLocal(String key) {
    	return localTable.get(key);
    }
    
    public boolean isSubClassOf(KraClass maybeSuper) {
    	if (superClass == null) {
    		return false;
    	}
    	
    	return (superClass.compareTo(maybeSuper) == 0);
    }

	@Override
	public int compareTo(Type o) {
		if (o == null) {
			return -1;
		}

		return this.getName().compareTo(o.getName());
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
}
