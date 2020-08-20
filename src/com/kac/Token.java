package com.kac;

public class Token {

    private final TokenType tokenType;
    private final String lexeme;
    private final Object value;
    private final int lineNumber;

    Token(TokenType tokenType, String lexeme, Object value, int lineNumber){
        this.tokenType = tokenType;
        this.lexeme = lexeme;
        this.value = value;
        this.lineNumber = lineNumber;
    }

    @Override
    public String toString(){
        return "TYPE:" + tokenType + " LEX:" + lexeme + " LINE:" + lineNumber;
    }
}
