package com.kac;

import java.util.List;

public class Parser {
    //list of tokens used to create abstract syntax tree
    private final List<Token> tokens;
    private int current = 0;

    public Parser(List<Token> tokens){
        this.tokens = tokens;
    }

    private Expr expression(){
        return equality();
    }

    private Expr equality(){
        return new Expr.Literal("token");
    }

    //utility functions

    private Token peek(){
        return tokens.get(current);
    }

    private boolean isAtEnd(){
        return peek().tokenType == TokenType.EOF;
    }

    private boolean match(TokenType... types){
        for(TokenType type : types){
            if(check(type)) {
                return true;
            }
        }
        return false;
    }

    private boolean check(TokenType type){
        if(isAtEnd())
            return false;
        return peek().tokenType == type;
    }

    private Token advance(){
        return tokens.get(current++);
    }
}
