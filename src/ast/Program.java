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
	}

	public void genC(PW pw) {
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
