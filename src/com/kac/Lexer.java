package com.kac;

import java.util.ArrayList;
import java.util.List;

public class Lexer {

    private String source;

    public Lexer(String source){
        this.source=source;
    }

    public List<Token> scanTokens(){
        System.out.println("scanning tokens");

        return new ArrayList<Token>();
    }
}
