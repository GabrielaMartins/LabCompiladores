
package comp;

import ast.*;
import lexer.*;
import java.io.*;
import java.util.*;

public class Compiler {

	// compile must receive an input with an character less than
	// p_input.lenght
	public Program compile(char[] input, PrintWriter outError) {

		ArrayList<CompilationError> compilationErrorList = new ArrayList<>();
		signalError = new SignalError(outError, compilationErrorList);
		symbolTable = new SymbolTable();
		lexer = new Lexer(input, signalError);
		signalError.setLexer(lexer);

		Program program = null;
		lexer.nextToken();
		program = program(compilationErrorList);
				
		return program;
	}

	private Program program(ArrayList<CompilationError> compilationErrorList) {
		// Program ::= KraClass { KraClass }
		ArrayList<MetaobjectCall> metaobjectCallList = new ArrayList<>();
		KraClassList kraClassList = new KraClassList();
		
		Program program = new Program(kraClassList, metaobjectCallList, compilationErrorList);
		try {
			while ( lexer.token == Symbol.MOCall ) {
				metaobjectCallList.add(metaobjectCall());
			}
			
			kraClassList.addElement(classDec());
			while ( lexer.token == Symbol.CLASS ) {
				kraClassList.addElement(classDec());
			}
			
			if ( lexer.token != Symbol.EOF ) {
				signalError.show("End of file expected");
			}
			
			//ER-SEM78: Programa sem classe Program
			if (kraClassList.getElement("Program") == null) {
				signalError.show("Source code without a class 'Program'");
			}
		}
		catch( RuntimeException e) {
			// if there was an exception, there is a compilation signalError
		}
		
		return program;
	}

	/**  parses a metaobject call as <code>{@literal @}ce(...)</code> in <br>
     * <code>
     * @ce(5, "'class' expected") <br>
     * clas Program <br>
     *     public void run() { } <br>
     * end <br>
     * </code>
     * 
	   
	 */
	@SuppressWarnings("incomplete-switch")
	private MetaobjectCall metaobjectCall() {
		String name = lexer.getMetaobjectName();
		lexer.nextToken();
		ArrayList<Object> metaobjectParamList = new ArrayList<>();
		if ( lexer.token == Symbol.LEFTPAR ) {
			// metaobject call with parameters
			lexer.nextToken();
			while ( lexer.token == Symbol.LITERALINT || lexer.token == Symbol.LITERALSTRING ||
					lexer.token == Symbol.IDENT ) {
				switch ( lexer.token ) {
				case LITERALINT:
					metaobjectParamList.add(lexer.getNumberValue());
					break;
				case LITERALSTRING:
					metaobjectParamList.add(lexer.getLiteralStringValue());
					break;
				case IDENT:
					metaobjectParamList.add(lexer.getStringValue());
				}
				lexer.nextToken();
				if ( lexer.token == Symbol.COMMA ) 
					lexer.nextToken();
				else
					break;
			}
			if ( lexer.token != Symbol.RIGHTPAR ) 
				signalError.show("')' expected after metaobject call with parameters");
			else
				lexer.nextToken();
		}
		//não entendi, ler gramática:
		if ( name.equals("nce") ) {
			if ( metaobjectParamList.size() != 0 )
				signalError.show("Metaobject 'nce' does not take parameters");
		}
		else if ( name.equals("ce") ) {
			if ( metaobjectParamList.size() != 3 && metaobjectParamList.size() != 4 )
				signalError.show("Metaobject 'ce' take three or four parameters");
			if ( !( metaobjectParamList.get(0) instanceof Integer)  )
				signalError.show("The first parameter of metaobject 'ce' should be an integer number");
			if ( !( metaobjectParamList.get(1) instanceof String) ||  !( metaobjectParamList.get(2) instanceof String) )
				signalError.show("The second and third parameters of metaobject 'ce' should be literal strings");
			if ( metaobjectParamList.size() >= 4 && !( metaobjectParamList.get(3) instanceof String) )  
				signalError.show("The fourth parameter of metaobject 'ce' should be a literal string");
			
		}
			
		return new MetaobjectCall(name, metaobjectParamList);
	}

	private KraClass classDec() {
		// Note que os métodos desta classe não correspondem exatamente às
		// regras
		// da gramática. Este método classDec, por exemplo, implementa
		// a produção KraClass (veja abaixo) e partes de outras produções.

		/*
		 * KraClass ::= ``class'' Id [ ``extends'' Id ] "{" MemberList "}"
		 * MemberList ::= { Qualifier Member } 
		 * Member ::= InstVarDec | MethodDec
		 * InstVarDec ::= Type IdList ";" 
		 * MethodDec ::= Qualifier Type Id "("[ FormalParamDec ] ")" "{" StatementList "}" 
		 * Qualifier ::= [ "static" ]  ( "private" | "public" )
		 */
		
		//3 tipos de qualifier: static, final e (public/private)
		Symbol classQualifier = null;
		Symbol finalQualifier = null;
		Symbol staticQualifier = null;
		
		Method met;
		KraClass superClass = null;
		
		if ( lexer.token == Symbol.FINAL) {
			classQualifier = Symbol.FINAL;
			lexer.nextToken();
		}
		
		if ( lexer.token != Symbol.CLASS ) signalError.show("'class' expected");
		lexer.nextToken();
		if ( lexer.token != Symbol.IDENT )
			signalError.show(SignalError.ident_expected);
		
		String className = lexer.getStringValue();
		String superclassName = null;
		if (symbolTable.getInGlobal(className) != null) {
			signalError.show("Class '" + className + "already declared");
		}
		
		lexer.nextToken();
		if ( lexer.token == Symbol.EXTENDS ) {
			lexer.nextToken();
			if ( lexer.token != Symbol.IDENT )
				signalError.show(SignalError.ident_expected);
			
			/*
			 * ER-SEM27: Classe herda de si mesma?
			 * ER-SEM83: Classe herda de classe final?
			 */
			superclassName = lexer.getStringValue();
			if (superclassName.equals(className)) {
				signalError.show("Class '" + className + "' is inheriting from itself");
			}
			
			//Superclasse foi declarada?
			superClass = symbolTable.getInGlobal(superclassName);
			if (superClass == null) {
				signalError.show("SuperClass '" + superclassName + "' is not declared");
			} else {
				if (superClass.isFinal()) {
					signalError.show("Class '" + className + "' is inheriting from final class "
								 	 + "'" + superclassName + "'");
				}
			}

			lexer.nextToken();
		}
		if ( lexer.token != Symbol.LEFTCURBRACKET )
			signalError.show("{ expected", true);
		lexer.nextToken();
		
		currentClass = new KraClass(className, classQualifier, superClass);
		
		if (lexer.token == Symbol.RIGHTCURBRACKET) {
			lexer.nextToken();
			symbolTable.putInGlobal(currentClass.getName(), currentClass);
			
			return currentClass;

		}
		
		boolean f = false; //flag pra controlar a primeira entrada no while abaixo
		while (lexer.token == Symbol.PRIVATE || lexer.token == Symbol.PUBLIC
				|| lexer.token == Symbol.FINAL || lexer.token == Symbol.STATIC) {
			
			f = true; // =)
			//verificar se uma variável é final ou static (só variáveis ou métodos tbm?)
			if (lexer.token == Symbol.FINAL) {
				finalQualifier = Symbol.FINAL;
				lexer.nextToken();
			}
			
			if (lexer.token == Symbol.STATIC) {
				staticQualifier = Symbol.STATIC;
				lexer.nextToken();
			}
			
			Symbol qualifier;
			
			switch (lexer.token) {
			case PRIVATE:
				lexer.nextToken();
				if (lexer.token == Symbol.STATIC) {
					signalError.show(signalError.ident_expected);
				}
				qualifier = Symbol.PRIVATE;
				break;
			case PUBLIC:
				lexer.nextToken();
				if (lexer.token == Symbol.STATIC) {
					signalError.show(signalError.ident_expected);
				}
				qualifier = Symbol.PUBLIC;
				break;
			default:
				signalError.show("private, or public expected");
				qualifier = Symbol.PUBLIC;
			}
			
			Type t = type();
			if ( lexer.token != Symbol.IDENT )
				signalError.show("Identifier expected");
			String name = lexer.getStringValue();
			lexer.nextToken();
			
			
			if ( lexer.token == Symbol.LEFTPAR ) {
				met = methodDec(t, name, qualifier, finalQualifier, staticQualifier);
				if (met.getQualifier() == Symbol.PUBLIC) {
					currentClass.addPublicMethod(met);
					System.out.println("Metodos publicos:");
					currentClass.printPublic();
				} else {
					currentClass.addPrivateMethod(met);
					System.out.println("Metodos privados:");
					currentClass.printPrivate();
				}
			} else if ( qualifier != Symbol.PRIVATE ) {
				//lista de variáveis que deve estar como private na classe 
				signalError.show("Attempt to declare public instance variable '" + name + "'");
			} else {
				
				currentClass.setInstanceVariableList(instanceVarDec(t, name));
				//instanceVarDec(t, name);
			}			
		}
		
		//Se qualifier não foi definido, não leu private ou public no while acima
		if (f == false) {
			signalError.show("'public', 'private', or '}' expected");
		}
		
		//ER-SEM77: Se for classe Program, deve ter metodo run()
		if (currentClass.getName().equals("Program")) {
			if (currentClass.searchMethod("run") == null) {
				signalError.show("Method 'run' was not found in class 'Program'");
			}
		}
		
		if ( lexer.token != Symbol.RIGHTCURBRACKET )
			signalError.show("public/private or \"}\" expected");
		lexer.nextToken();		
		
		symbolTable.putInGlobal(currentClass.getName(), currentClass);
		
		return currentClass;
	}
//feito
	private InstanceVariableList instanceVarDec(Type type, String name) {
		// InstVarDec ::= [ "static" ] "private" Type IdList ";"
		
		InstanceVariable var;
		InstanceVariableList listVar = new InstanceVariableList();
		
		var = (InstanceVariable) symbolTable.getInLocal(name);
		if(var == null){
			var = new InstanceVariable(name, type);
			symbolTable.putInLocal(name, var);
			listVar.addElement(var);
		}else{
			signalError.show("Variable " + name + " is being redeclared");
		}
			
		while (lexer.token == Symbol.COMMA) {
			lexer.nextToken();
			if ( lexer.token != Symbol.IDENT )
				signalError.show("Identifier expected");
			
			String variableName = lexer.getStringValue();
			var = (InstanceVariable) symbolTable.getInLocal(variableName);
			
			if (var == null) {
				//Se a variável não está na tabela então coloca
				//variáveis de instancia não seriam globais?
				var = new InstanceVariable(variableName, type);
				symbolTable.putInLocal(variableName, var);
				listVar.addElement(var);				
			} else {
				signalError.show("Variable " + name + " is being redeclared");
			}			
			
			lexer.nextToken();
		}
		
		if ( lexer.token != Symbol.SEMICOLON )
			signalError.show(SignalError.semicolon_expected);
		lexer.nextToken();
		
		//Após processar as variáveis de instância, já pode limpar a localTable.
		symbolTable.removeLocalIdent();
		
		return listVar;
	}

	private Method methodDec(Type type, String name, Symbol qualifier, Symbol finalQualifier, Symbol staticQualifier) {
		/*
		 * MethodDec ::= Qualifier Return Id "("[ FormalParamDec ] ")" "{"
		 *                StatementList "}"
		 */
			
		//ER-SEM85: Método final em classe final
		if (currentClass.isFinal() && finalQualifier != null) {
			signalError.show("'final' method in a 'final' class");
		}
		
		//ER-SEM31: Método com nome de variável
		if (currentClass.searchVariable(name) != null) {
			signalError.show("Method '" + name + "' has name equal to an instance variable");
		}
		
		currentMethod = new Method(name, type, qualifier);
		if (finalQualifier != null) currentMethod.setFinal();
		if (staticQualifier != null) currentMethod.setStatic();
		
		lexer.nextToken();
		if ( lexer.token != Symbol.RIGHTPAR ) {
			currentMethod.setParamList(formalParamDec());
		}
		if ( lexer.token != Symbol.RIGHTPAR ) signalError.show("')' expected");
		
		/* Tratamento de erros no método run()
		 * ER-SEM79: run() não deve ter parâmetros
		 * ER-SEM80: Retorno diferente de void
		 * ER-SEM81: Metodo deve ser public
		 * ER-SEM82: run não pode ser static
		 */
		if (currentClass.getName().equals("Program") && name.equals("run")) {

			if (staticQualifier != null) {
				signalError.show("Method 'run' cannot be static");
			}

			if (qualifier != Symbol.PUBLIC) {
				signalError.show("Method 'run' of class 'Program' cannot be private");
			}

			if (type != Type.voidType) {
				signalError.show("Method 'run' of class 'Program' with a return " +
						"value type different from 'void'");
			}
			
			if (currentMethod.getParamList().getSize() > 0) {
				signalError.show("Method 'run' of class 'Program' cannot take parameters");
			}
		}
		
		/*Tratamento dos erros:
		* ER-SEM73: Redefinição de método static
		* ER-SEM32: Método sendo redeclarado
		* ER-SEM84: Método final sendo redefinido na classe derivada
		* 
		* ER-SEM29 e ER-SEM30: Erro na redefinição de métodos com assinaturas diferentes
		*/
		
		//Se não for nulo, achou algum parametro igual
		if (currentClass.searchMethod(currentMethod.getName()) != null) {
			
			if (currentMethod.isStatic()) {
				signalError.show("Redefinition of static method '" + currentMethod.getName() +"'");
			} else {
				signalError.show("Method '" + currentMethod.getName() + "' is being redeclared");
			}
		} else if (currentClass.hasSuper()) {
			Method searchM;
			if ( (searchM = currentClass.searchMethodS(currentMethod.getName())) != null) {
				if (searchM.isFinal()) {
					signalError.show("Redeclaration of final method '" + searchM.getName() + "'");
				}
				//Se não é final pode ser redefinido na classe derivada, mas com mesma assinatura
				if (searchM.compareTo(currentMethod) != 0) {
					signalError.show("Method '" + currentMethod.getName() + "' of subclass '" 
									 + currentClass.getCname() + "' has a signature different from "
									 + "method inherited from superclass '" 
									 + currentClass.getSuper().getName() + "'");
				}
			}
			
		}

		lexer.nextToken();
		if ( lexer.token != Symbol.LEFTCURBRACKET ) signalError.show("{ expected");

		lexer.nextToken();
		statementList();
		if ( lexer.token != Symbol.RIGHTCURBRACKET ) signalError.show("} expected");

		lexer.nextToken();
		
		return currentMethod;

	}
	
//feito
	private void localDec() {
		// LocalDec ::= Type IdList ";"
		ArrayList<Variable> varLocalList = new ArrayList<Variable>();
		Variable v;

		Type type = type();

		if ( lexer.token != Symbol.IDENT ) signalError.show("Identifier expected");
		
		//verifica de váriável já foi declarada
		
		String name = lexer.getStringValue();
		v = (Variable)symbolTable.get(name);
		
		if(v==null){
			v = new Variable(name, type);
			symbolTable.putInLocal(name, v);
		}else{
			signalError.show("Variable " + name + " is being redeclared");
		}
		
		varLocalList.add(v);
		
		lexer.nextToken();
		
		v = null;
		name = null;
		
		while (lexer.token == Symbol.COMMA) {
			lexer.nextToken();
			
			if ( lexer.token != Symbol.IDENT )
				//ER-SIN02: arrumando mensagem de erro
				signalError.show("Missing identifier");
			name = lexer.getStringValue();
			v = (Variable)symbolTable.get(name);
			
			if(v==null){
				v = new Variable(name, type);
				symbolTable.putInLocal(name, v);
			}else{
				signalError.show("Variable " + name + " is being redeclared");
			}
			
			varLocalList.add(v);
			lexer.nextToken();
		
		}
		
		if(lexer.token != Symbol.SEMICOLON){
			signalError.show("Missing ';'");
		}
		
		//return varLocalList;
	}

	private ParameterList formalParamDec() {
		// FormalParamDec ::= ParamDec { "," ParamDec }
		
		ParameterList paramList = new ParameterList();
		
		paramList.addElement(paramDec());
		while (lexer.token == Symbol.COMMA) {
			lexer.nextToken();
			paramList.addElement(paramDec());
		}
		
		return paramList;
	}

	private Parameter paramDec() {
		// ParamDec ::= Type Id
		Type t = null;
		String name = null;
		
		t = type();
		if ( lexer.token != Symbol.IDENT ) {
			signalError.show("Identifier expected");
			return new Parameter("Param", t);
		}
		name = lexer.getStringValue();
		lexer.nextToken();
		
		return new Parameter(name, t);
	}

	private Type type() {
		// Type ::= BasicType | Id
		Type result = null;

		switch (lexer.token) {
		case VOID:
			result = Type.voidType;
			break;
		case INT:
			result = Type.intType;
			break;
		case BOOLEAN:
			result = Type.booleanType;
			break;
		case STRING:
			result = Type.stringType;
			break;
		case IDENT:
			// # corrija: faça uma busca na TS para buscar a classe
			// IDENT deve ser uma classe.
			//imagino que a classe retorna um tipo "ident" e o nome do tipo é armazenado em algum lugar 
			String nameType = lexer.getStringValue();
			
			
			//corrigindo: ER-SEM18
			
			if(isType(nameType)== true){
				//result = Type.identType;
				result = new TypeIdent(nameType);
			}else{
				
				signalError.show("Type " +nameType+ " was not found");
			}
			break;
		default:
			signalError.show("Type expected");
			result = Type.undefinedType;
		}
		lexer.nextToken();
		return result;
	}

	private void compositeStatement() {

		lexer.nextToken();
		statementList();
		if ( lexer.token != Symbol.RIGHTCURBRACKET )
			signalError.show("} expected");
		else
			lexer.nextToken();
	}

	private void statementList() {
		// CompStatement ::= "{" { Statement } "}"
		Symbol tk;
		// statements always begin with an identifier, if, read, write, ...
		while ((tk = lexer.token) != Symbol.RIGHTCURBRACKET
				&& tk != Symbol.ELSE)
			statement();
	}

	private void statement() {
		/*
		 * Statement ::= Assignment ``;'' | IfStat |WhileStat | MessageSend
		 *                ``;'' | ReturnStat ``;'' | ReadStat ``;'' | WriteStat ``;'' |
		 *               ``break'' ``;'' | ``;'' | CompStatement | LocalDec
		 */

		switch (lexer.token) {
		case THIS:
		case IDENT:
		case SUPER:
		case INT:
		case BOOLEAN:
		case STRING:
			assignExprLocalDec();
			break;
		case RETURN:
			returnStatement();
			break;
		case READ:
			readStatement();
			break;
		case WRITE:
			writeStatement();
			break;
		case WRITELN:
			writelnStatement();
			break;
		case IF:
			ifStatement();
			break;
		case BREAK:
			breakStatement();
			break;
		case WHILE:
			whileStatement();
			break;
		case SEMICOLON:
			nullStatement();
			break;
		case LEFTCURBRACKET:
			compositeStatement();
			break;
		default:
			//ER-SEM06
			if(lexer.token == Symbol.LITERALINT || lexer.token == Symbol.LITERALSTRING ||
			lexer.token == Symbol.FALSE || lexer.token == Symbol.TRUE)
				signalError.show("'operator expected' or 'variable expected at the left-hand side of a assignment'");
			
			signalError.show("Statement expected");
		}
	}

	/*
	 * retorne true se 'name' é uma classe declarada anteriormente. É necessário
	 * fazer uma busca na tabela de símbolos para isto.
	 */
	private boolean isType(String name) {
		return this.symbolTable.getInGlobal(name) != null;
	}
	
	/*
	 * retorna true se 'name' é uma variável declarada localmente
	 * ou uma variável de instancia da classe corrente.
	 */
	private boolean isVariable(String name) {
		
		if (this.symbolTable.getInLocal(name) != null) {
			return true;
		}
		
		return this.currentClass.searchVariable(name) != null;
	}
	
	private boolean isMethod(String name) {
		return currentClass.callMethod(name) != null;
	}

	/*
	 * AssignExprLocalDec ::= Expression [ ``$=$'' Expression ] | LocalDec
	 */
	private Expr assignExprLocalDec() {
   		Expr left = null;
		Expr right = null;
		
		if ( lexer.token == Symbol.INT || lexer.token == Symbol.BOOLEAN
				|| lexer.token == Symbol.STRING ||
				// token é uma classe declarada textualmente antes desta
				// instrução
				
				//a merda tá dando aqui :
				//tem que verificar se o ident é um tipo, se ele for, manda pro localdec
				//mas se ele não for, já assume que é uma variável. Mas pode ser que nem 
				//Err-SEM18, onde é um tipo que não tem uma classe, então teria que dar um erro
				//de não ter encontrado o tipo. Se eu fizer a verificação no else, ele pode
				//ser uma variável e não um tipo
				(lexer.token == Symbol.IDENT && isType(lexer.getStringValue())) ) {
			/*
			 * uma declaração de variável. 'lexer.token' é o tipo da variável
			 * 
			 * AssignExprLocalDec ::= Expression [ ``$=$'' Expression ] | LocalDec 
			 * LocalDec ::= Type IdList ``;''
			 */
			localDec();
		}
		else {
			/*
			 * AssignExprLocalDec ::= Expression [ ``$=$'' Expression ]
			 */
			//verificando se uma variável foi declarada {ER-SEM02}
			if(lexer.token == Symbol.IDENT){
				String name = lexer.getStringValue();
				Variable var = symbolTable.getInLocal(name);
				
				//Gabriela
				if(var == null){
					//Valdeir
					var = currentClass.searchVariable(name);
					if (var != null) {
						signalError.show("Identifier '"+ name +"' was not found");
					}
					//Valdeir$
					
					if (var == null) {
						signalError.show("Variable " + name + " was not declared");
					}
				}
				//$Gabriela
			}
			
			left = expr();
			
			if ( lexer.token == Symbol.ASSIGN ) {
				lexer.nextToken();
				right = expr();
				//ER-SEM04
				if(left.getType() != right.getType()){
					if(left.getType()== Type.booleanType && right.getType() == Type.intType){
						signalError.show("\'int\' cannot be assigned to \'boolean\'");
					}
					if(left.getType()== Type.intType && right.getType() == Type.booleanType){
						signalError.show("Type error: value of the right-hand side is not subtype of the variable of the left-hand side.");
					}
					
					if(left.getType()instanceof TypeIdent && 
							(right.getType() == Type.intType || right.getType() == Type.booleanType || right.getType() == Type.stringType)){
						signalError.show("Type error: the type of the expression of the right-hand side is a basic type and the type of the variable of the left-hand side is a class");
					}
					
					if((left.getType() == Type.intType || left.getType() == Type.booleanType || left.getType() == Type.stringType) &&
							right.getType()instanceof TypeIdent){
						signalError.show("Type error: type of the left-hand side of the assignment is a basic type and the type of the right-hand side is a class");
					}
				
					//ER-SEM43.KRA
					if((left.getType() == Type.intType || left.getType() == Type.booleanType || left.getType() == Type.stringType) && 
							right.getType()== Type.nullType){
						signalError.show("Type error: 'null' cannot be assigned to a variable of a basic type");
					}
				}
				
				//ER-SEM38
				if(left.getType() instanceof TypeIdent && right.getType() instanceof TypeIdent){
					String typeLeftName = left.getType().getName();
					String typeRightName = right.getType().getName();
					
					KraClass typeLeft = symbolTable.getInGlobal(typeLeftName);
					KraClass typeRight = symbolTable.getInGlobal(typeRightName);
					
					
					if((typeLeft.getSuper() != null ) && typeLeft.getSuper().getName() == typeRight.getName()){
						signalError.show("Type error: type of the right-hand side of the assignment is not a subclass of the left-hand side");
					}
					
				}
				
				if ( lexer.token != Symbol.SEMICOLON )
					signalError.show("Missing ';'", true);
				else
					lexer.nextToken();
				
			}
		}
		
		return null;
	}

	private ExprList realParameters() {
		ExprList anExprList = null;

		if ( lexer.token != Symbol.LEFTPAR ) signalError.show("( expected");
		lexer.nextToken();
		if ( startExpr(lexer.token) ) anExprList = exprList();
		if ( lexer.token != Symbol.RIGHTPAR ) signalError.show(") expected");
		lexer.nextToken();
		return anExprList;
	}

	private void whileStatement() {
		//ER-SEM11.KRA
		Expr condition;
		lexer.nextToken();
		if ( lexer.token != Symbol.LEFTPAR ) signalError.show("( expected");
		lexer.nextToken();
		condition = expr();
		if(condition.getType()!= Type.booleanType){
			signalError.show("non-boolean expression in  'while' command");
		}
		if ( lexer.token != Symbol.RIGHTPAR ) signalError.show(") expected");
		lexer.nextToken();
		statement();
	}

	private void ifStatement() {

		lexer.nextToken();
		if ( lexer.token != Symbol.LEFTPAR ) signalError.show("( expected");
		lexer.nextToken();
		expr();
		if ( lexer.token != Symbol.RIGHTPAR ) signalError.show(") expected");
		lexer.nextToken();
		statement();
		if ( lexer.token == Symbol.ELSE ) {
			lexer.nextToken();
			statement();
		}
	}

	private void returnStatement() {

		lexer.nextToken();
		expr();
		if ( lexer.token != Symbol.SEMICOLON )
			signalError.show(SignalError.semicolon_expected);
		lexer.nextToken();
	}

	private void readStatement() {
		lexer.nextToken();
		if ( lexer.token != Symbol.LEFTPAR ) signalError.show("( expected");
		lexer.nextToken();
		while (true) {
			if ( lexer.token == Symbol.THIS ) {
				lexer.nextToken();
				if ( lexer.token != Symbol.DOT ) signalError.show(". expected");
				lexer.nextToken();
			}
			if ( lexer.token != Symbol.IDENT )
				signalError.show(SignalError.ident_expected);

			String name = lexer.getStringValue();
			lexer.nextToken();
			if ( lexer.token == Symbol.COMMA )
				lexer.nextToken();
			else
				break;
		}

		if ( lexer.token != Symbol.RIGHTPAR ) signalError.show(") expected");
		lexer.nextToken();
		if ( lexer.token != Symbol.SEMICOLON )
			signalError.show(SignalError.semicolon_expected);
		lexer.nextToken();
	}

	private void writeStatement() {

		lexer.nextToken();
		if ( lexer.token != Symbol.LEFTPAR ) signalError.show("( expected");
		lexer.nextToken();
		exprList();
		if ( lexer.token != Symbol.RIGHTPAR ) signalError.show(") expected");
		lexer.nextToken();
		if ( lexer.token != Symbol.SEMICOLON )
			signalError.show(SignalError.semicolon_expected);
		lexer.nextToken();
	}

	private void writelnStatement() {

		lexer.nextToken();
		if ( lexer.token != Symbol.LEFTPAR ) signalError.show("( expected");
		lexer.nextToken();
		exprList();
		if ( lexer.token != Symbol.RIGHTPAR ) signalError.show(") expected");
		lexer.nextToken();
		if ( lexer.token != Symbol.SEMICOLON )
			signalError.show(SignalError.semicolon_expected);
		lexer.nextToken();
	}

	private void breakStatement() {
		lexer.nextToken();
		if ( lexer.token != Symbol.SEMICOLON )
			signalError.show(SignalError.semicolon_expected);
		lexer.nextToken();
	}

	private void nullStatement() {
		lexer.nextToken();
	}

	private ExprList exprList() {
		// ExpressionList ::= Expression { "," Expression }

		ExprList anExprList = new ExprList();
		anExprList.addElement(expr());
		while (lexer.token == Symbol.COMMA) {
			lexer.nextToken();
			anExprList.addElement(expr());
		}
		return anExprList;
	}

	private Expr expr() {

		Expr left = simpleExpr();
		Symbol op = lexer.token;
		if ( op == Symbol.EQ || op == Symbol.NEQ || op == Symbol.LE
				|| op == Symbol.LT || op == Symbol.GE || op == Symbol.GT ) {
			lexer.nextToken();
			Expr right = simpleExpr();
			left = new CompositeExpr(left, op, right);
		}
		return left;
	}

	private Expr simpleExpr() {
		Symbol op;

		Expr left = term();
		while ((op = lexer.token) == Symbol.MINUS || op == Symbol.PLUS
				|| op == Symbol.OR) {
			lexer.nextToken();
			Expr right = term();
			left = new CompositeExpr(left, op, right);
		}
		return left;
	}

	private Expr term() {
		Symbol op;

		Expr left = signalFactor();
		while ((op = lexer.token) == Symbol.DIV || op == Symbol.MULT
				|| op == Symbol.AND) {
			lexer.nextToken();
			Expr right = signalFactor();
			left = new CompositeExpr(left, op, right);
			
		}
		return left;
	}

	private Expr signalFactor() {
		Symbol op;
		if ( (op = lexer.token) == Symbol.PLUS || op == Symbol.MINUS ) {
			lexer.nextToken();
			return new SignalExpr(op, factor());
		}
		else
			return factor();
	}

	/*
	 * Factor ::= BasicValue | "(" Expression ")" | "!" Factor | "null" |
	 *      ObjectCreation | PrimaryExpr
	 * 
	 * BasicValue ::= IntValue | BooleanValue | StringValue 
	 * BooleanValue ::=  "true" | "false" 
	 * ObjectCreation ::= "new" Id "(" ")" 
	 * PrimaryExpr ::= "super" "." Id "(" [ ExpressionList ] ")"  | 
	 *                 Id  |
	 *                 Id "." Id | 
	 *                 Id "." Id "(" [ ExpressionList ] ")" |
	 *                 Id "." Id "." Id "(" [ ExpressionList ] ")" |
	 *                 "this" | 
	 *                 "this" "." Id | 
	 *                 "this" "." Id "(" [ ExpressionList ] ")"  | 
	 *                 "this" "." Id "." Id "(" [ ExpressionList ] ")"
	 */
	private Expr factor() {

		Expr e;
		ExprList exprList = null;
		String messageName, ident;

		switch (lexer.token) {
		// IntValue
		case LITERALINT:
			return literalInt();
			// BooleanValue
		case FALSE:
			lexer.nextToken();
			return LiteralBoolean.False;
			// BooleanValue
		case TRUE:
			lexer.nextToken();
			return LiteralBoolean.True;
			// StringValue
		case LITERALSTRING:
			String literalString = lexer.getLiteralStringValue();
			lexer.nextToken();
			return new LiteralString(literalString);
			// "(" Expression ")" |
		case LEFTPAR:
			lexer.nextToken();
			e = expr();
			if ( lexer.token != Symbol.RIGHTPAR ) signalError.show(") expected");
			lexer.nextToken();
			return new ParenthesisExpr(e);

			// "null"
		case NULL:
			lexer.nextToken();
			return new NullExpr();
			// "!" Factor
		case NOT:
			lexer.nextToken();
			e = expr();
			//Gabriela
			//ER-SEM15.KRA
			if(e.getType()==Type.intType){
				signalError.show("Operator '!' does not accepts 'int' values");
			}
			//Gabriela$
			return new UnaryExpr(e, Symbol.NOT);
			// ObjectCreation ::= "new" Id "(" ")"
		case NEW:
			lexer.nextToken();
			if ( lexer.token != Symbol.IDENT )
				signalError.show("Identifier expected");

			String className = lexer.getStringValue();
			
			/*
			 * // encontre a classe className in symbol table KraClass 
			 *      aClass = symbolTable.getInGlobal(className); 
			 *      if ( aClass == null ) ...
			 */
				
			//Gabriela
			//ER-SEM37 - 38 - 86
			KraClass aClass = symbolTable.getInGlobal(className);
			if(aClass == null){
				signalError.show("Class '" + className + "' was not found");
			}
			
			Type t = type();
			
			exprList = this.realParameters();
			
			//lexer.nextToken();
			
			System.out.println("O que temos aqui é" + lexer.token);
			
			
			/*
			 * return an object representing the creation of an object
			 */
			
			return new NewExpr(t, exprList);
			
			//$Gabriela
			
			/*
          	 * PrimaryExpr ::= "super" "." Id "(" [ ExpressionList ] ")"  | 
          	 *                 Id  |
          	 *                 Id "." Id | 
          	 *                 Id "." Id "(" [ ExpressionList ] ")" |
          	 *                 Id "." Id "." Id "(" [ ExpressionList ] ")" |
          	 *                 "this" | 
          	 *                 "this" "." Id | 
          	 *                 "this" "." Id "(" [ ExpressionList ] ")"  | 
          	 *                 "this" "." Id "." Id "(" [ ExpressionList ] ")"
			 */
		case SUPER:
			// "super" "." Id "(" [ ExpressionList ] ")"
			//Valdeir
			if (currentClass.getSuper() == null) {
				signalError.show("'super' used in class '" + currentClass.getName()
								 + "' that does not have a superclass");
			}
			//Valdeir$
			lexer.nextToken();
			if ( lexer.token != Symbol.DOT ) {
				signalError.show("'.' expected");
			}
			else
				lexer.nextToken();
			if ( lexer.token != Symbol.IDENT )
				signalError.show("Identifier expected");
			
			//Gabriela ER-SEM47
			messageName = lexer.getStringValue();
			if(currentClass.searchMethodS(messageName)==null){
				signalError.show("Method '"+ messageName +"' was not found in superclass '" + currentClass.getName() + "' or its superclasses");
			}
			//$Gabriela
			
			//Valdeir
			//ER-SEM60: Método privado em super
			if (currentClass.getSuper().callMethod(messageName) == null) {
				signalError.show("Method '" + messageName + "' was not found in the "
								 + "public interface of '" + currentClass.getName()
								 + "' or its superclasses");
			}
			//Valdeir$
			/*
			 * para fazer as conferências semânticas, procure por 'messageName'
			 * na superclasse/superclasse da superclasse etc
			 */
			lexer.nextToken();
			exprList = realParameters();
			break;
		case IDENT:
			/*
          	 * PrimaryExpr ::=  
          	 *                 Id  |
          	 *                 Id "." Id | 
          	 *                 Id "." Id "(" [ ExpressionList ] ")" |
          	 *                 Id "." Id "." Id "(" [ ExpressionList ] ")" |
			 */

			String firstId = lexer.getStringValue();
			lexer.nextToken();
			if ( lexer.token != Symbol.DOT ) {
				// Id
				// retorne um objeto da ASA que representa um identificador
				
				//Arrumado: colocando como retorno um variableExpr 
				Variable var = symbolTable.getInLocal(firstId);
				//Valdeir
				if (var == null) {
					var = currentClass.searchVariable(firstId);
				}
				//Valdeir$
				
				return new VariableExpr(var);
			}
			else { // Id "."
				lexer.nextToken(); // coma o "."
				if ( lexer.token != Symbol.IDENT ) {
					signalError.show("Identifier expected");
				}
				
				else {
					// Id "." Id
					
					//Valdeir
					//Se não é uma classe é um tipo básico
					//ER-SEM07: Enviando mensagem para classe básica
					KraClass idType = getClass(firstId);
					if (idType == null) {
						signalError.show("Message send to a non-object receiver");
					}
					//Valdeir$
									
					lexer.nextToken();
//<<<<<<< HEAD
					ident = lexer.getStringValue();					
/*=======
					ident = lexer.getStringValue();
					//Gabriela
					KraClass c = getClass(firstId);
					Method m = c.callMethod(ident);
					if(m == null){
						signalError.show("Method 'set' was not found in class 'A' or its superclasses");
					}
					//$Gabriela
					
>>>>>>> factorGabi */
					if ( lexer.token == Symbol.DOT ) {
						// Id "." Id "." Id "(" [ ExpressionList ] ")"
						/*
						 * se o compilador permite variáveis estáticas, é possível
						 * ter esta opção, como
						 *     Clock.currentDay.setDay(12);
						 * Contudo, se variáveis estáticas não estiver nas especificações,
						 * sinalize um erro neste ponto.
						 */
						lexer.nextToken();
						if ( lexer.token != Symbol.IDENT )
							signalError.show("Identifier expected");
						
						//Valdeir
						messageName = lexer.getStringValue();
						
						//Valdeir$
						lexer.nextToken();
						exprList = this.realParameters();

					}
					else if ( lexer.token == Symbol.LEFTPAR ) {
						// Id "." Id "(" [ ExpressionList ] ")"
						
						//Gabriela ER-SEM61
						Method m = idType.searchMethod(ident);
						if(m==null){
							m = idType.searchMethodS(ident);
							if(m==null){
								signalError.show("Method '"+ ident+ "' was not found in class '" + idType.getName()+ "' or its superclasses");
							}
						}
						//$Gabriela
						
						//Valdeir
						//ER-SEM59: Chamada a método privado
						m = idType.callMethod(ident);
						if (m == null) {
							signalError.show("Method '" + ident + "' was not found in the "
											+ "public interface of '" + idType.getName()
											+ "' or its superclasses");
						}
						
						/*if (m.isStatic() && isType(firstId) == false) {
							
						}*/
						//Valdeir$
						
						exprList = this.realParameters();
						/*
						 * para fazer as conferências semânticas, procure por
						 * método 'ident' na classe de 'firstId'
						 */
					}
					else {
						// retorne o objeto da ASA que representa Id "." Id
					}
				}
			}
			break;
		case THIS:
			Type type = null;
			/*
			 * Este 'case THIS:' trata os seguintes casos: 
          	 * PrimaryExpr ::= 
          	 *                 "this" | 
          	 *                 "this" "." Id | 
          	 *                 "this" "." Id "(" [ ExpressionList ] ")"  | 
          	 *                 "this" "." Id "." Id "(" [ ExpressionList ] ")"
			 */
			lexer.nextToken();
			if ( lexer.token != Symbol.DOT ) {
				// only 'this'
				// retorne um objeto da ASA que representa 'this'
				// confira se não estamos em um método estático
				
				//pode retornar só this?
				return null;
			}
			else {
				lexer.nextToken();
				if ( lexer.token != Symbol.IDENT )
					signalError.show("Identifier expected");
				ident = lexer.getStringValue();
				
				lexer.nextToken();
				// já analisou "this" "." Id
				if ( lexer.token == Symbol.LEFTPAR ) {
					// "this" "." Id "(" [ ExpressionList ] ")"
					/*
					 * Confira se a classe corrente possui um método cujo nome é
					 * 'ident' e que pode tomar os parâmetros de ExpressionList
					 */
					
					//Gabriela
					Method m = currentClass.callMethod(ident);
					if(m==null){
						signalError.show("Method '"+ ident+ "' was not found in class '" + currentClass.getName()+ "' or its superclasses");
					}
					type = m.getType();
					//Gabriela$
					
					exprList = this.realParameters();
				}
				else if ( lexer.token == Symbol.DOT ) {
					// "this" "." Id "." Id "(" [ ExpressionList ] ")"
					lexer.nextToken();
					if ( lexer.token != Symbol.IDENT )
						signalError.show("Identifier expected");
					lexer.nextToken();
					exprList = this.realParameters();
				}
				else {
					// retorne o objeto da ASA que representa "this" "." Id
					/*
					 * confira se a classe corrente realmente possui uma
					 * variável de instância 'ident'
					 */
					
					//Gabriela
					InstanceVariable var = currentClass.searchVariable(ident);
					if(var==null){
						signalError.show("Instance variable '"+ ident+ "' was not found in class '" + currentClass.getName()+ "'");
					}
					if(type == null){
						type = var.getType();
					}
					//$Gabriela
					
					return new ThisExpr(type, exprList);
				}
			}
			break;
		default:
			signalError.show("Expression expected");
		}
		return null;
	}

	private LiteralInt literalInt() {

		LiteralInt e = null;

		// the number value is stored in lexer.getToken().value as an object of
		// Integer.
		// Method intValue returns that value as an value of type int.
		int value = lexer.getNumberValue();
		lexer.nextToken();
		return new LiteralInt(value);
	}

	private static boolean startExpr(Symbol token) {

		return token == Symbol.FALSE || token == Symbol.TRUE
				|| token == Symbol.NOT || token == Symbol.THIS
				|| token == Symbol.LITERALINT || token == Symbol.SUPER
				|| token == Symbol.LEFTPAR || token == Symbol.NULL
				|| token == Symbol.IDENT || token == Symbol.LITERALSTRING;

	}
	
	//Gabriela
	private KraClass getClass(String name) {
		String varType = null;
		Variable var = symbolTable.getInLocal(name);
		if (var != null) {
			varType = var.getType().getName();
		}
		
		KraClass className = symbolTable.getInGlobal(varType);
		
		return className;
	}
	//Gabriela$

	private SymbolTable		symbolTable;
	private Lexer			lexer;
	private SignalError		signalError;
	private KraClass 		currentClass;
	private Method			currentMethod;

}
