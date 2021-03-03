package com.kac;

import java.util.HashMap;

public class Class {

    public String name;
    public Environment publicVariables;
    public Environment privateVariables;
    public HashMap<String, Stmt.FunctionDeclaration> publicFunctions;
    public HashMap<String, Stmt.FunctionDeclaration> privateFunctions;

    Class(){}

    public void setPublicVariables(Environment publicVariables) {
        this.publicVariables = publicVariables;
    }

    public void setPrivateVariables(Environment privateVariables) {
        this.privateVariables = privateVariables;
    }

    public void setPublicFunctions(HashMap<String, Stmt.FunctionDeclaration> publicFunctions) {
        this.publicFunctions = publicFunctions;
    }

    public void setPrivateFunctions(HashMap<String, Stmt.FunctionDeclaration> privateFunctions) {
        this.privateFunctions = privateFunctions;
    }
}
