package com.kac;

import java.util.List;

public class Parser {
    //list of tokens used to create abstract syntax tree
    private final List<Token> tokens;
    private int current = 0;

    public Parser(List<Token> tokens){
        this.tokens = tokens;
    }

    //each grammar rule will be separate method
    //I'll use recursive descent parsing, it means
    //traversing from lowest precedence to highest
    
}
