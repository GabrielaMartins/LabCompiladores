package ast;

import java.util.*;

public class ParameterList {

    public ParameterList() {
       paramList = new ArrayList<Parameter>();
    }
    
    public void genKra(PW pw){
    	
    }

    public void addElement(Parameter p) {
       paramList.add(p);
    }

    public Iterator<Parameter> elements() {
        return paramList.iterator();
    }

    public int getSize() {
        return paramList.size();
    }

    private ArrayList<Parameter> paramList;
}
