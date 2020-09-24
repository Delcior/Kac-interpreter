package com.kac;

enum TokenType{
    EOF,

    //keywords
    IF, ELSE, WHILE, AND, OR, FOR,
    TRUE, FALSE, RETURN, PRINT,
    SUPER, THIS, VAR, FUN,

    //literal
    STRING, NUMBER, NULL, USER_DEFINED,

    //binary tokens
    EXCL_MARK_EQUAL, EQUAL,
    EQUAL_EQUAL, LESS, LESS_EQUAL,
    GREATER, GREATER_EQUAL,
    STAR, SLASH, PLUS, MINUS,

    //single character tokens
    EXCL_MARK, SEMICOLON,  DOT, COMMA, RIGHT_BRACE,
    LEFT_BRACE, RIGHT_PAREN, LEFT_PAREN
}