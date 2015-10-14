/* 
 * Universidade Federal de São Carlos - Campus Sorocaba
 * Laboratório de Compiladores - 2015/2
 * Projeto: Fase 1
 * 
 * Docente:		José Guimarães
 * 
 * Dicentes:	Gabriela de Jesus Martins	- 489689
 * 				Valdeir Soares Perozim		- 489786	
 */

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
		whileStack = new Stack<Integer>();

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
			while ( lexer.token == Symbol.CLASS || lexer.token == Symbol.FINAL ) {
				kraClassList.addElement(classDec());
			}
			
			if ( lexer.token != Symbol.EOF ) {
				signalError.show("End of file expected");
			}
			
			//ER-SEM78: Programa sem classe Program
			if (kraClassList.getElement("Program") == null) {
				signalError.show("Source code without a class 'Program'");
			}
			
			program.setClassList(kraClassList);
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
				signalError.show("Class expected");
			
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
			signalError.show("'{' expected", true);
		lexer.nextToken();
		
		currentClass = new KraClass(className, classQualifier, superClass);
		symbolTable.putInGlobal(currentClass.getName(), currentClass);
		
		if (lexer.token == Symbol.RIGHTCURBRACKET) {
			lexer.nextToken();
			
			return currentClass;
		}
		
		while (lexer.token == Symbol.PRIVATE || lexer.token == Symbol.PUBLIC
				|| lexer.token == Symbol.FINAL || lexer.token == Symbol.STATIC) {
			
			Symbol qualifier;
			Symbol finalQualifier = null;
			Symbol staticQualifier = null;

			//verificar se uma variável é final ou static (só variáveis ou métodos tbm?)
			if (lexer.token == Symbol.FINAL) {
				finalQualifier = Symbol.FINAL;
				lexer.nextToken();
			}
			
			if (lexer.token == Symbol.STATIC) {
				staticQualifier = Symbol.STATIC;
				lexer.nextToken();
			}
					
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
				} else {
					currentClass.addPrivateMethod(met);
				}
				symbolTable.putInGlobal(currentClass.getName(), currentClass);
			} else if ( qualifier != Symbol.PRIVATE ) {
				//lista de variáveis que deve estar como private na classe 
				signalError.show("Attempt to declare public instance variable '" + name + "'");
			} else {
				
				boolean isStatic = (staticQualifier != null) ? true : false;
				currentClass.addVariableList(instanceVarDec(t, name, isStatic));
				symbolTable.putInGlobal(currentClass.getName(), currentClass);
			}			
		}
		
		//Se qualifier não foi definido, não leu private ou public no while acima
		//ER-SIN31:
		if ( lexer.token != Symbol.RIGHTCURBRACKET ) {
			signalError.show("'public', 'private', or '}' expected");
		}
		
		//ER-SEM77: Se for classe Program, deve ter metodo run()
		if (currentClass.getName().equals("Program")) {
			if (currentClass.searchMethod("run") == null) {
				signalError.show("Method 'run' was not found in class 'Program'");
			}
		}
		
		
		lexer.nextToken();
		
		symbolTable.putInGlobal(currentClass.getName(), currentClass);
		
		return currentClass;
	}
//feito
	private InstanceVariableList instanceVarDec(Type type, String name, boolean isStatic) {
		// InstVarDec ::= [ "static" ] "private" Type IdList ";"
		
		InstanceVariable var;
		InstanceVariableList listVar = new InstanceVariableList();
		
		//Cada classe tem sua propria localTable, deixamos symbolTable.localTable
		//para variaveis locais!
		var = (InstanceVariable) currentClass.getInLocal(name);
		if(var == null || var.isStatic() && ! isStatic){
			var = new InstanceVariable(name, type, isStatic);
			currentClass.putInLocal(name, var);
			listVar.addElement(var);
		}else{
			signalError.show("Variable '" + name + "' is being redeclared");
		}
			
		while (lexer.token == Symbol.COMMA) {
			lexer.nextToken();
			if ( lexer.token != Symbol.IDENT )
				signalError.show("Identifier expected");
			
			String variableName = lexer.getStringValue();
			var = (InstanceVariable) currentClass.getInLocal(variableName);
			
			//Verificação para permitir váriaveis estáticas e não estáticas de mesmo nome
			if (var == null || var.isStatic() && ! isStatic) {
				//Se a variável não está na tabela então coloca
				var = new InstanceVariable(variableName, type, isStatic);
				currentClass.putInLocal(variableName, var);
				listVar.addElement(var);				
			} else {
				signalError.show("Variable '" + name + "' is being redeclared");
			}			
			
			lexer.nextToken();
		}
		
		if ( lexer.token != Symbol.SEMICOLON )
			signalError.show(SignalError.semicolon_expected);
		lexer.nextToken();
		
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
		if (currentClass.getInLocal(name) != null) {
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
		* ER-SEM32: Método sendo redeclarado
		* ER-SEM73: Redefinição de método static
		* ER-SEM84: Método final sendo redefinido na classe derivada
		* 
		* ER-SEM29 e ER-SEM30: Erro na redefinição de métodos com assinaturas diferentes
		*/
		
		//Se não for nulo, achou algum parametro igual
		Method searchM;
		if (currentClass.searchMethod(currentMethod.getName()) != null) {
			signalError.show("Method '" + currentMethod.getName() + "' is being redeclared");
		} else if ( (searchM = currentClass.searchStaticMethod(currentMethod.getName())) != null) {
			if (currentMethod.isStatic()) {
				signalError.show("Redefinition of static method '" + currentMethod.getName() +"'");
			}
		} else if (currentClass.hasSuper()) {
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
		if ( lexer.token != Symbol.LEFTCURBRACKET ) signalError.show("'{' expected");
		lexer.nextToken();
		
		//ER-SEM01: Falta de retorno em método com return type diferente de void
		StatementList sl = statementList();
		if (type != Type.voidType && sl.getElement(StatementType.Return) == null) {
			signalError.show("Missing 'return' statement in method '" + name + "'");
		}
		currentMethod.setStatementList(sl);
		
		if ( lexer.token != Symbol.RIGHTCURBRACKET ) signalError.show("} expected");

		lexer.nextToken();
		
		symbolTable.removeLocalIdent(); //apos avaliar o metodo pode limpar a localTable
		
		return currentMethod;

	}
	
	private LocalVariableList localDec() {
		// LocalDec ::= Type IdList ";"
		
		Variable v;
		LocalVariableList localVarList = new LocalVariableList();

		Type type = type();

		if ( lexer.token != Symbol.IDENT ) signalError.show("Identifier expected");
		
		//verifica de váriável já foi declarada
		
		String name = lexer.getStringValue();
		//v = (Variable) symbolTable.get(name);
		v = symbolTable.getInLocal(name);
		
		if(v==null){
			v = new Variable(name, type);
			//symbolTable.putInLocal(name, v);
			symbolTable.putInLocal(name, v);
			localVarList.addElement(v);//?
		}else{
			signalError.show("Variable " + name + " is being redeclared");
		}
		
		lexer.nextToken();
			
		while (lexer.token == Symbol.COMMA) {
			lexer.nextToken();
			
			//ER-SIN02: arrumando mensagem de erro
			if ( lexer.token != Symbol.IDENT )
				signalError.show("Missing identifier");
			
			name = lexer.getStringValue();
			//v = (Variable) symbolTable.get(name);
			v = symbolTable.getInLocal(name);
			
			if(v==null){
				v = new Variable(name, type);
				//symbolTable.putInLocal(name, v);
				symbolTable.putInLocal(name, v);
				localVarList.addElement(v);
			}else{
				signalError.show("Variable " + name + " is being redeclared");
			}
			
			lexer.nextToken();
		
		}
		
		if(lexer.token != Symbol.SEMICOLON){
			signalError.show("Missing ';'");
		}
		
		currentMethod.addVariableList(localVarList);
		return localVarList;
	}
	
	private LocalVariableList localDecType(String typeName) {
		// LocalDec ::= Type IdList ";"
		
		Variable v;
		LocalVariableList localVarList = new LocalVariableList();

		if ( lexer.token != Symbol.IDENT ) signalError.show("Identifier expected");
		
		if (! isType(typeName)) {
			signalError.show("Type '" + typeName + "' was not found");
		}
		
		Type type = symbolTable.getInGlobal(typeName);
		
		//verifica de váriável já foi declarada
		
		String name = lexer.getStringValue();
		//v = (Variable) symbolTable.get(name);
		v = symbolTable.getInLocal(name);
		
		if(v==null){
			v = new Variable(name, type);
			//symbolTable.putInLocal(name, v);
			symbolTable.putInLocal(name, v);
			localVarList.addElement(v);//?
		}else{
			signalError.show("Variable " + name + " is being redeclared");
		}
		
		lexer.nextToken();
			
		while (lexer.token == Symbol.COMMA) {
			lexer.nextToken();
			
			//ER-SIN02: arrumando mensagem de erro
			if ( lexer.token != Symbol.IDENT )
				signalError.show("Missing identifier");
			
			name = lexer.getStringValue();
			//v = (Variable) symbolTable.get(name);
			v = symbolTable.getInLocal(name);
			
			if(v==null){
				v = new Variable(name, type);
				//symbolTable.putInLocal(name, v);
				symbolTable.putInLocal(name, v);
				localVarList.addElement(v);
			}else{
				signalError.show("Variable " + name + " is being redeclared");
			}
			
			lexer.nextToken();
		
		}
		
		if(lexer.token != Symbol.SEMICOLON){
			signalError.show("Missing ';'");
		}
		
		currentMethod.addVariableList(localVarList);
		
		return localVarList;
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
		Parameter p;
		
		t = type();
		if ( lexer.token != Symbol.IDENT ) {
			signalError.show("Identifier expected");
		}
		name = lexer.getStringValue();
		
		p = (Parameter) symbolTable.getInLocal(name);
		if (p != null) {
			signalError.show("Variable '" + name + "' is being redeclared");
		} else {
			p = new Parameter(name, t);
		}
		
		symbolTable.putInLocal(name, p);
		
		lexer.nextToken();
				
		return p;
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
				result = symbolTable.getInGlobal(nameType);
			}else{
				signalError.show("Type " + nameType + " was not found");
			}
			break;
		default:
			signalError.show("Type expected");
			result = Type.undefinedType;
		}
		lexer.nextToken();
		return result;
	}

	private CompositeStatement compositeStatement() {
		StatementList s;
		lexer.nextToken();
		s = statementList();
		if ( lexer.token != Symbol.RIGHTCURBRACKET )
			signalError.show("} expected");
		else
			lexer.nextToken();
		
		return new CompositeStatement(s);
	}

	private StatementList statementList() {
		// CompStatement ::= "{" { Statement } "}"
		Symbol tk;
		StatementList list = new StatementList();
		// statements always begin with an identifier, if, read, write, ...
		while ((tk = lexer.token) != Symbol.RIGHTCURBRACKET
				&& tk != Symbol.ELSE) {
			list.addElement(statement());
		}
		
		return list;
	}

	private Statement statement() {
		/*
		 * Statement ::= Assignment ``;'' | IfStat |WhileStat | MessageSend
		 *                ``;'' | ReturnStat ``;'' | ReadStat ``;'' | WriteStat ``;'' |
		 *               ``break'' ``;'' | ``;'' | CompStatement | LocalDec
		 */
		
		Statement ret = null;
		
		switch (lexer.token) {
		case THIS:
		case IDENT:
		case SUPER:
		case INT:
		case BOOLEAN:
		case STRING:
			ret = assignExprLocalDec();
			break;
		case RETURN:
			ret = returnStatement();
			break;
		case READ:
			ret = readStatement();
			break;
		case WRITE:
			ret = writeStatement();
			break;
		case WRITELN:
			ret = writelnStatement();
			break;
		case IF:
			ret = ifStatement();
			break;
		case BREAK://?
			breakStatement();
			break;
		case WHILE:
			ret = whileStatement();
			break;
		case SEMICOLON://?
			nullStatement();
			break;
		case LEFTCURBRACKET:
			ret = compositeStatement();
			break;
		default:
			//ER-SEM06
			if(lexer.token == Symbol.LITERALINT || lexer.token == Symbol.LITERALSTRING ||
					lexer.token == Symbol.FALSE || lexer.token == Symbol.TRUE) {
				
				signalError.show("'operator expected' or 'variable expected at the left-hand side of a assignment'");
			}
			
			signalError.show("Statement expected");
		}
		
		return ret;
	}

	/*
	 * retorne true se 'name' é uma classe declarada anteriormente. É necessário
	 * fazer uma busca na tabela de símbolos para isto.
	 */
	private boolean isType(String name) {
		KraClass classe = this.symbolTable.getInGlobal(name);
		
		return classe != null;
	}

	/*
	 * AssignExprLocalDec ::= Expression [ ``$=$'' Expression ] | LocalDec
	 */
	private AssignmentStatement assignExprLocalDec() {
   		Expr left = null;
		Expr right = null;
		
		if ( lexer.token == Symbol.INT || lexer.token == Symbol.BOOLEAN
				|| lexer.token == Symbol.STRING ) {
			/*
			 * uma declaração de variável. 'lexer.token' é o tipo da variável
			 * 
			 * AssignExprLocalDec ::= Expression [ ``$=$'' Expression ] | LocalDec 
			 * LocalDec ::= Type IdList ``;''
			 */
			localDec();
		} else {
			/*
			 * AssignExprLocalDec ::= Expression [ ``$=$'' Expression ]
			 */
			
			left = expr();
			
			if ( lexer.token == Symbol.ASSIGN ) {
				
				if (left instanceof VariableExpr) {
					String varName = ((VariableExpr) left).getV().getName();
					Variable v = symbolTable.getInLocal(varName);
					if (v == null) {
						signalError.show("Variable '" + varName + "' was not declared");
					}
					
					left = new VariableExpr(v);
				}
				
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
					
					if(left.getType()instanceof KraClass && 
							(right.getType() == Type.intType || right.getType() == Type.booleanType || right.getType() == Type.stringType)){
						signalError.show("Type error: the type of the expression of the right-hand side is a basic type and the type of the variable of the left-hand side is a class");
					}
					
					if((left.getType() == Type.intType || left.getType() == Type.booleanType || left.getType() == Type.stringType) &&
							right.getType()instanceof KraClass){
						signalError.show("Type error: type of the left-hand side of the assignment is a basic type and the type of the right-hand side is a class");
					}
				
					//ER-SEM43.KRA
					if((left.getType() == Type.intType || left.getType() == Type.booleanType || left.getType() == Type.stringType) && 
							right.getType()== Type.nullType){
						signalError.show("Type error: 'null' cannot be assigned to a variable of a basic type");
					}
				}
				
				//ER-SEM38
				if(left.getType() instanceof KraClass && right.getType() instanceof KraClass){
					String typeLeftName = left.getType().getName();
					String typeRightName = right.getType().getName();
					
					KraClass typeLeft = symbolTable.getInGlobal(typeLeftName);
					KraClass typeRight = symbolTable.getInGlobal(typeRightName);
					
					
					if((typeLeft.getSuper() != null ) && typeLeft.getSuper().getName() == typeRight.getName()){
						signalError.show("Type error: type of the right-hand side of the assignment is not a subclass of the left-hand side");
					}
					
				}
				
				
				if(right.getType()==Type.nullType){
					Variable var = ((VariableExpr)left).getV();
					symbolTable.removeVarLocalIdent(var.getName(), var);
					var.setIsNull(true);
					symbolTable.putInLocal(var.getName(), var);
				}
				
				//ER-SEM36: Retornando void para uma variavel
				if (right.getType() == Type.voidType) {
					signalError.show("Expression expected in the right-hand side of assignment");
				}
				
				if ( lexer.token != Symbol.SEMICOLON )
					signalError.show("Missing ';'", true);
				else
					lexer.nextToken();
				
			} else {
				//Assume que é um possível tipo
				if (left instanceof VariableExpr) {
					String typeName = ((VariableExpr) left).getV().getName();
					localDecType(typeName);
				}
				
				if (left instanceof MessageSendToVariable ||
						left instanceof MessageSendToSuper ||
						left instanceof MessageSendToSelf) {
					
					if (left.getType() != Type.voidType) {
						signalError.show("Message send returns a value that is not used");
					}
				}
			}
		}
		
		return new AssignmentStatement(left, right);
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

	private WhileStatement whileStatement() {
		whileStack.push(1);
		//ER-SEM11.KRA
		Expr condition;
		Statement s;
		lexer.nextToken();
		if ( lexer.token != Symbol.LEFTPAR ) signalError.show("( expected");
		lexer.nextToken();
		condition = expr();
		if(condition.getType()!= Type.booleanType){
			signalError.show("non-boolean expression in  'while' command");
		}
		if ( lexer.token != Symbol.RIGHTPAR ) signalError.show(") expected");
		lexer.nextToken();
		s = statement();
		if (whileStack.isEmpty() == false) whileStack.pop();
		
		return new WhileStatement(condition, s);
	}

	private IfStatement ifStatement() {
		Expr e;
		StatementList s = null;
		lexer.nextToken();
		if ( lexer.token != Symbol.LEFTPAR ) signalError.show("( expected");
		lexer.nextToken();
		if (lexer.token == Symbol.RIGHTPAR) {
			signalError.show("Expression expected");
		}
		//tem que verificar se essa expressão é boolean?
		e = expr();
		if ( lexer.token != Symbol.RIGHTPAR ) signalError.show(") expected");
		lexer.nextToken();
		s.addElement(statement()); 
		if ( lexer.token == Symbol.ELSE ) {
			lexer.nextToken();
			s.addElement(statement()); 
		}
		
		return new IfStatement(e, s);
	}

	private ReturnStatement returnStatement() {
		Expr e;
		
		//ER-SEM35: Retorno em método void
		if (currentMethod.getType() == Type.voidType) {
			signalError.show("Illegal 'return' statement. Method returns 'void'");
		}
		
		lexer.nextToken();
		
		e = expr();
		//Ta bugando muitos erros ainda, vou deixar comentado por enquanto
		//ER-SEM39: Retorno com tipos incompativeis
		if(e.getType() instanceof KraClass && currentMethod.getType() instanceof KraClass){
			String typeReturned = e.getType().getName();
			String typeOfMethod = currentMethod.getType().getName();

			KraClass typeLeft = symbolTable.getInGlobal(typeReturned);
			KraClass typeRight = symbolTable.getInGlobal(typeOfMethod);
			
			if (! typeReturned.equals(typeOfMethod)) {
				if (typeLeft.getSuper() == null) {
					signalError.show("Type error: type of the expression returned is not subclass of the method return type");
				} else if (typeLeft.getSuper().getName() != typeRight.getName()) {
					signalError.show("Type error: type of the expression returned is not subclass of the method return type");
				}
			}
		}
		
		if ( lexer.token != Symbol.SEMICOLON )
			signalError.show(SignalError.semicolon_expected);
		lexer.nextToken();
		
		return new ReturnStatement(e);
	}

	private ReadStatement readStatement() {
		boolean isInstance = false;
		
		Variable var = null;
		
		lexer.nextToken();
		if ( lexer.token != Symbol.LEFTPAR ) signalError.show("'(' expected after 'read' command");
		lexer.nextToken();
		if (lexer.token == Symbol.RIGHTPAR) {
			signalError.show("Command 'read' without arguments");
		}
		
		while (true) {
			if ( lexer.token == Symbol.THIS ) {
				lexer.nextToken();
				if ( lexer.token != Symbol.DOT ) signalError.show(". expected");
				lexer.nextToken();
				isInstance = true;
			}
			if ( lexer.token != Symbol.IDENT )
				signalError.show("Command 'read' expects a variable");

			String name = lexer.getStringValue();
			//ER-SEM13 - 45
			if(isInstance == true){
				var = (InstanceVariable)currentClass.getInLocal(name);
				if(var != null && var.getType()==Type.booleanType){
					signalError.show("Command 'read' does not accept '" + var.getType().getName() + "' variables");
				}
				
				if(var!=null && var.getType() instanceof KraClass){
					signalError.show("'int' or 'String' expression expected");
				}
				
				if(var == null){
					signalError.show("Instance variable " + name + " was not declared");
				}
			}
			
			if(isInstance == false){
				var = null;
				var = symbolTable.getInLocal(name);
				
				if(var != null && var.getType()==Type.booleanType){
					signalError.show("Command 'read' does not accept '" + var.getType().getName() +"' variables");
				}
				
				if(var!=null && var.getType() instanceof KraClass){
					signalError.show("'int' or 'String' expression expected");
				}
				
				if(var == null){
					signalError.show("Variable " + name + " was not declared");
				}
			}
			
			lexer.nextToken();
			if ( lexer.token == Symbol.COMMA ) {
				lexer.nextToken();
				//ER-SIN05: quando fica uma , sem um ident ou this depois
				if (lexer.token != Symbol.IDENT && lexer.token != Symbol.THIS) {
					signalError.show("Expression expected");
				}
			} else
				break;
		}

		if ( lexer.token != Symbol.RIGHTPAR ) signalError.show(") expected");
		lexer.nextToken();
		if ( lexer.token != Symbol.SEMICOLON )
			signalError.show(SignalError.semicolon_expected);
		lexer.nextToken();
		
		return new ReadStatement(var);
	}

	private WriteStatement writeStatement() {
		ExprList expr;
		
		lexer.nextToken();
		if ( lexer.token != Symbol.LEFTPAR ) signalError.show("Missing '('");
		lexer.nextToken();
		if (lexer.token == Symbol.RIGHTPAR) {
			signalError.show("Command 'write' without arguments");
		}
		
		expr = exprList();
		//ER-SEM14 - 44
		Iterator<Expr> it = expr.getExprList().iterator();
		
		while(it.hasNext()){
			Expr e = (Expr)it.next();
			if(e.getType() == Type.booleanType){
				signalError.show("Command 'write' does not accept '"+ Type.booleanType.getName() +"' expressions");
			}
			if(e.getType()instanceof KraClass){
				signalError.show("Command 'write' does not accept objects");
			}
			
			//tratar para quando o tipo é void?
			
		}
		
		if ( lexer.token != Symbol.RIGHTPAR ) signalError.show("')' expected");
		lexer.nextToken();
		if ( lexer.token != Symbol.SEMICOLON )
			signalError.show(SignalError.semicolon_expected);
		lexer.nextToken();
		
		return new WriteStatement(expr);
	}

	private WritelnStatement writelnStatement() {
		ExprList expr;
		
		lexer.nextToken();
		if ( lexer.token != Symbol.LEFTPAR ) signalError.show("( expected");
		lexer.nextToken();
		expr = exprList();
		
		//ER-SEM14 - 44
		Iterator<Expr> it = expr.getExprList().iterator();

		while(it.hasNext()){
			Expr e = (Expr)it.next();
			if(e.getType() == Type.booleanType){
				signalError.show("Command 'write' does not accept '"+ Type.booleanType.getName() +"' expressions");
			}

		}
		
		if ( lexer.token != Symbol.RIGHTPAR ) signalError.show("')' expected");
		lexer.nextToken();
		if ( lexer.token != Symbol.SEMICOLON )
			signalError.show(SignalError.semicolon_expected);
		lexer.nextToken();
		
		return new WritelnStatement(expr);
	}

	private void breakStatement() {//?
		
		if (whileStack.isEmpty()) {
			signalError.show("Command 'break' outside a command 'while'");
		}		
		
		lexer.nextToken();
		if ( lexer.token != Symbol.SEMICOLON )
			signalError.show(SignalError.semicolon_expected);
		lexer.nextToken();
	}

	private void nullStatement() {//?
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
			//ER-SEM57 ER-SEM58
			
			Variable var = null;
			
			if(left.getType()instanceof KraClass){
				var = ((VariableExpr)left).getV();
				if(var.getIsNull() == true && (op == Symbol.NEQ || op == Symbol.EQ)){
					signalError.show("Incompatible types cannot be compared with '" + op.toString()+ "' because the result will always be 'false'");
				}
			}
			
			if(right.getType()instanceof KraClass){
				var = ((VariableExpr)right).getV();
				if(var.getIsNull()==true && (op == Symbol.NEQ || op == Symbol.EQ)){
					signalError.show("Incompatible types cannot be compared with '" + op.toString()+ "' because the result will always be 'false'");
				}
			}
			
			if(!checkRelExpr(left.getType(), right.getType())){
				signalError.show("Type error in expression");
			}
			
			left = new CompositeExpr(left, op, right);
		}
		return left;
	}
	
	private boolean checkRelExpr( Type left, Type right ) {
			return left == right;
	}

	private Expr simpleExpr() {
		Symbol op;

		Expr left = term();
		while ((op = lexer.token) == Symbol.MINUS || op == Symbol.PLUS
				|| op == Symbol.OR) {
			lexer.nextToken();
			Expr right = term();
			//ER-SEM08 - SEM09
			if(left.getType()!= Type.intType && 
					(op == Symbol.MINUS || op==Symbol.PLUS)){
				signalError.show("type " + left.getType().getName()+ " does not support operation '" + op.toString() + "'");
			}
			
			if(right.getType()!= Type.intType && 
					(op == Symbol.MINUS || op==Symbol.PLUS)){
				signalError.show("operator '" + op.toString() + "' of '" + left.getType().getName() + "' expects an '" + left.getType().getName()+"' value");
			}
			
			if(op==Symbol.OR){
				if(!checkBooleanExpr(left.getType(),right.getType())){
					if(left.getType()!= Type.booleanType){
						signalError.show("type '" + left.getType().getName() + "' does not support operator '||'");
					}
					
					if(right.getType()!= Type.booleanType){
						signalError.show("type '" + right.getType().getName() + "' does not support operator '||'");
					}
					
				}
			}
			
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
			//ER-SEM09 - erro de linha
			Expr right = signalFactor();
			if(left.getType() != Type.intType && 
					(op == Symbol.DIV || op == Symbol.MULT)){
				signalError.show("type '" + left.getType().getName() + "' does not support operator '" + op.toString()+ "'");
			}
			if(right.getType() != Type.intType && 
					(op == Symbol.DIV || op == Symbol.MULT)){
				signalError.show("type '" + left.getType().getName() + "' does not support operator '" + op.toString()+ "'");
			}
			
			if(op == Symbol.AND){
				if(!checkBooleanExpr(left.getType(),right.getType())){
					if(left.getType()!= Type.booleanType){
						signalError.show("type '" + left.getType().getName() + "' does not support operator '&&'");
					}
					
					if(right.getType()!= Type.booleanType){
						signalError.show("type '" + right.getType().getName() + "' does not support operator '&&'");
					}
					
				}
			}
			
			
			left = new CompositeExpr(left, op, right);
			
		}
		return left;
	}
	
	private boolean checkBooleanExpr( Type left, Type right ) {
		return left == Type.booleanType && right == Type.booleanType;
	}

	private Expr signalFactor() {
		Symbol op;
		Expr e;
		if ( (op = lexer.token) == Symbol.PLUS || op == Symbol.MINUS ) {
			lexer.nextToken();
			//ERR-SEM16
			e = factor();
			if(e.getType() != Type.intType)
				signalError.show("Operator '" + op.toString() + "' does not accepts '" + e.getType().getName() + "' expressions");
				
			return new SignalExpr(op, e);
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
		Method m;

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
			
			//ER-SEM15.KRA
			if(e.getType()==Type.intType){
				signalError.show("Operator '!' does not accepts 'int' values");
			}
			
			return new UnaryExpr(e, Symbol.NOT);
			// ObjectCreation ::= "new" Id "(" ")"
		case NEW:
			lexer.nextToken();
			if ( lexer.token != Symbol.IDENT )
				signalError.show("Identifier expected");

			String className = lexer.getStringValue();
			lexer.nextToken();
			
			/*
			 * // encontre a classe className in symbol table KraClass 
			 *      aClass = symbolTable.getInGlobal(className); 
			 *      if ( aClass == null ) ...
			 */
				
			
			//ER-SEM37 - 38 - 86
			KraClass aClass = symbolTable.getInGlobal(className);
			if(aClass == null){
				signalError.show("Class '" + className + "' was not found");
			}
			
			//ER-SIN58: Construtores não devem ter parâmetro
			if (lexer.token != Symbol.LEFTPAR) {
				signalError.show("'(' expected");
			}
			lexer.nextToken();
			if (lexer.token != Symbol.RIGHTPAR) {
				signalError.show("')' expected");
			}
			lexer.nextToken();
			
			//exprList = this.realParameters();
			
			//lexer.nextToken();		
			
			/*
			 * return an object representing the creation of an object
			 */
			
			return new NewObject(aClass);
						
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

			//ER-SEM46: super em classe sem herança
			if (currentClass.getSuper() == null) {
				signalError.show("'super' used in class '" + currentClass.getName()
								 + "' that does not have a superclass");
			}
			
			//ER: Super não pode ser chamado em metodo static
			if (currentMethod.isStatic()) {
				signalError.show("super used in static method '" + currentMethod.getName() + "'");
			}

			lexer.nextToken();
			if ( lexer.token != Symbol.DOT ) {
				signalError.show("'.' expected");
			}
			else
				lexer.nextToken();
			if ( lexer.token != Symbol.IDENT )
				signalError.show("Identifier expected");
			
			//ER-SEM47: Método inexistente em super
			messageName = lexer.getStringValue();
			m = currentClass.searchMethodS(messageName);
			if(m == null){
				signalError.show("Method '"+ messageName +"' was not found in superclass '" 
								 + currentClass.getName() + "' or its superclasses");
			}
			
			//ER-SEM60: Método privado em super
			if (m.isPrivate()) {
				signalError.show("Method '" + messageName + "' was not found in the "
								 + "public interface of '" + currentClass.getSuper().getName()
								 + "' or its superclasses");
			}
			
			/*
			 * para fazer as conferências semânticas, procure por 'messageName'
			 * na superclasse/superclasse da superclasse etc
			 */
			lexer.nextToken();
			exprList = realParameters();
			return new MessageSendToSuper(m, exprList);
					
		case IDENT:
			/*
          	 * PrimaryExpr ::=  
          	 *                 Id  |
          	 *                 Id "." Id | 
          	 *                 Id "." Id "(" [ ExpressionList ] ")" |
          	 *                 Id "." Id "." Id "(" [ ExpressionList ] ")" |
			 */

			String firstId = lexer.getStringValue();
			KraClass idType = null;
			
			lexer.nextToken();
			if ( lexer.token != Symbol.DOT ) {
				// Id
				// retorne um objeto da ASA que representa um identificador
				
				//ER-SEM63
				if (lexer.token == Symbol.LEFTPAR) {
					if (currentClass.searchStaticMethod(firstId) != null) {
						signalError.show("'.' or '=' expected after an identifier OR statement expected");
					}
				}
				
				Variable var;
				if (lexer.token == Symbol.IDENT || lexer.token == Symbol.ASSIGN) {
					var = new Variable(firstId, Type.undefinedType);
					return new VariableExpr(var);
				}
				
				var = symbolTable.getInLocal(firstId);
				if (var == null) {
					signalError.show("Variable '" + firstId + "' was not declared");
				}
				
				//Arrumado: colocando como retorno um variableExpr 
				
				return new VariableExpr(var);
			} else { // Id "."
				lexer.nextToken(); // coma o "."
				if ( lexer.token != Symbol.IDENT ) {
					signalError.show("Identifier expected");
				} else {
					// Id "." Id
									
					lexer.nextToken();
					ident = lexer.getStringValue();					
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
						
						messageName = lexer.getStringValue();
						
						lexer.nextToken();
						exprList = this.realParameters();

					} else if ( lexer.token == Symbol.LEFTPAR ) {
						// Id "." Id "(" [ ExpressionList ] ")"						
						//Method m;
						
						//É objeto recebendo mensagem
						//ER-SEM59: Chamada a método privado
						if (isType(firstId) == false) {
							
							idType = getClass(symbolTable.getInLocal(firstId).getType().getName());
							
							//ER-SEM07: Mensagem sendo enviada para tipo básico
							if (idType == null) {
								signalError.show("Message send to a non-object receiver");
							}
							
							m = this.getMethod(firstId, ident);
							if (m == null) {
								signalError.show("Method '" + ident + "' was not found in the "
												+ "public interface of '" + idType.getName()
												+ "' or its superclasses");
							}
						} else {
						
							//Senão considera que firtsId é classe!
							idType = getClass(firstId);
							m = idType.callStaticMethod(ident);
							if (m == null) {
								signalError.show("Static method '" + ident + "' was not found in class '" 
												 + idType.getName() + "'");
							}

							//A.id fora da classe A
							if (idType.getName().equals(currentClass.getName()) == false) {

								if (m.getQualifier() == Symbol.PRIVATE) {
									signalError.show("Method '" + ident + "' was not found in class"
													 + "' " + idType.getName() + "' or its superclasses");
								}
							}
						}
						
						exprList = this.realParameters();
						Variable var = symbolTable.getInLocal(firstId);
						return new MessageSendToVariable(var, m, exprList);
						/*
						 * para fazer as conferências semânticas, procure por
						 * método 'ident' na classe de 'firstId'
						 */
					} else {
						// retorne o objeto da ASA que representa Id "." Id
					}
				}
			}
			break;
		case THIS:
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
				
				//ER-SEM71: Chamada a this em método static
				if (currentMethod.isStatic()) {
					signalError.show("Attempt to access an instance variabel using 'this' in a static method");
				}
				
				return new MessageSendToSelf(currentClass);
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
					
					//ER-SEM71: Chamada a this em método static
					if (currentMethod.isStatic()) {
						signalError.show("Attempt to access an instance variabel using 'this' in a static method");
					}
					
					m = currentClass.searchMethod(ident);
					if(m==null){
						signalError.show("Method '"+ ident+ "' was not found in class '" 
										 + currentClass.getName()+ "' or its superclasses");
					}
					
					exprList = this.realParameters();
					
					//Tratar erro de passagem de parâmetros incorretos
					
					//return new MessageSendToSelf(currentClass, m, exprList);
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
					
					InstanceVariable var = (InstanceVariable) currentClass.getInLocal(ident);
					if(var==null){
						signalError.show("Instance variable '" + ident + "' was not found in class '"
										 + currentClass.getName()+ "'");
					}
					
					//ER-SEM71: Chamada a this em método static
					if (currentMethod.isStatic()) {
						signalError.show("Attempt to access an instance variabel using 'this' in a static method");
					}
					
					return new MessageSendToSelf(currentClass, var);
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
	
	private KraClass getClass(String name) {
		
		String varType = null;
		KraClass className = null;
		
		//Variable var = symbolTable.getInLocal(name);
		Variable var = symbolTable.getInLocal(name);
		if (var != null) {
			varType = var.getType().getName();
			className = symbolTable.getInGlobal(varType);
		} else {
			className = symbolTable.getInGlobal(name);
			if (className == null) {
				if (currentClass.getName().equals(name)) {
					className = currentClass;
				}
			}
		}
		
		return className;
	}
	
	
	private Method getMethod(String ident, String name) {
		
		if (name.equals(currentMethod.getName())) {
			return currentMethod;
		}
		
		if (isType(ident)) {
			return symbolTable.getInGlobal(ident).searchStaticMethod(name);
		}
		
		String tipo = symbolTable.getInLocal(ident).getType().getName();
		KraClass classe = symbolTable.getInGlobal(tipo);
		
		return classe.callMethod(name);
		
	}

	private SymbolTable		symbolTable;
	private Lexer			lexer;
	private SignalError		signalError;
	private KraClass 		currentClass;
	private Method			currentMethod;
	private Stack<Integer>	whileStack;	
}
