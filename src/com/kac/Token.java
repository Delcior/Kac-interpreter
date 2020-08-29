package com.kac;

public class Token {

    final TokenType tokenType;
    final String lexeme;
    final Object value;
    final int lineNumber;

    Token(TokenType tokenType, String lexeme, Object value, int lineNumber){
        this.tokenType = tokenType;
        this.lexeme = lexeme;
        this.value = value;
        this.lineNumber = lineNumber;
    }

    @Override
    public String toString(){
        return "TYPE:" + tokenType + " LEX:" + lexeme + " VALUE:" + value +" LINE:" + lineNumber;
    }
}