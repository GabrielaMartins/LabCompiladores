/*
 * Gabriela de Jesus Martins	- 489689
 * Valdeir Soares Perozim		- 489786
 */

package ast;

import java.util.*;
import comp.CompilationError;

public class Program {
	
	private KraClassList classList;
	private ArrayList<MetaobjectCall> metaobjectCallList;
	
	ArrayList<CompilationError> compilationErrorList;

	public Program(KraClassList classList, ArrayList<MetaobjectCall> metaobjectCallList, 
			       ArrayList<CompilationError> compilationErrorList) {
		this.classList = classList;
		this.metaobjectCallList = metaobjectCallList;
		this.compilationErrorList = compilationErrorList;
	}

	public void genKra(PW pw) {
		
		classList.genKra(pw);
	}

	public void genC(PW pw) {
		pw.println("#include <stdio.h>");
		pw.println("#include <stdlib.h>");
		pw.println("#include <malloc.h>");
		pw.println("");
		pw.println("typedef int boolean");
		pw.println("#define true 1");
		pw.println("#define false 0");
		pw.println("");
		pw.println("typedef");
		pw.add();
		pw.printlnIdent("void (*Func)();");
		pw.sub();
		pw.println("");
		
		classList.genC(pw);
	}
	
	public void setClassList(KraClassList classList) {
		this.classList = classList;
	}
	
	public KraClassList getClassList() {
		return classList;
	}


	public ArrayList<MetaobjectCall> getMetaobjectCallList() {
		return metaobjectCallList;
	}
	

	public boolean hasCompilationErrors() {
		return compilationErrorList != null && compilationErrorList.size() > 0 ;
	}

	public ArrayList<CompilationError> getCompilationErrorList() {
		return compilationErrorList;
	}
}
