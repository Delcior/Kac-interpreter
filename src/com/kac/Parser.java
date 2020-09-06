package com.kac;

import java.awt.event.PaintEvent;
import java.util.List;

public class Parser {
    private class ParserError extends RuntimeException {}
    //list of tokens used to create abstract syntax tree
    private final List<Token> tokens;
    private int current = 0;

    public Parser(List<Token> tokens){
        this.tokens = tokens;
    }

    public Expr parse(){
        try{
            return expression();
        }catch (ParserError er){
            return null;
        }
    }
    //methods are in order of precendence level(from lowest to highest)
    private Expr expression(){
        return equality();
    }

    private Expr equality(){
        Expr expression = comparison();

        while(match(TokenType.EXCL_MARK_EQUAL, TokenType.EQUAL_EQUAL)){
            Token operator = previous();
            Expr comparisonLeft = comparison();
            expression = new Expr.Binary(expression, operator, comparisonLeft);
        }

        return expression;
    }

    private Expr comparison(){
        Expr expression = addition();

        while(match(TokenType.GREATER, TokenType.GREATER_EQUAL, TokenType.LESS, TokenType.LESS_EQUAL)){
            Token operator = previous();
            Expr additionLeft = addition();
            expression = new Expr.Binary(expression, operator, additionLeft);
        }

        return expression;
    }

    private Expr addition(){
        Expr expression = multiplication();

        while(match(TokenType.MINUS, TokenType.PLUS)){
            Token operator = previous();
            Expr multiplicationLeft = multiplication();
            expression = new Expr.Binary(expression, operator, multiplicationLeft);
        }

        return expression;
    }

    private Expr multiplication(){
        Expr expression = unary();

        while(match(TokenType.SLASH, TokenType.STAR)){
            Token operator = previous();
            Expr unaryRight = unary();
            expression = new Expr.Binary(expression, operator, unaryRight);
        }

        return expression;
    }

    private Expr unary(){
        if(match(TokenType.EXCL_MARK, TokenType.MINUS)){
            Token operator = previous();
            Expr unary = unary();

            return new Expr.Unary(operator, unary);
        }

        return primary();
    }

    private Expr primary(){
        if(match(TokenType.NUMBER, TokenType.STRING))
            return new Expr.Literal(previous().value);

        if(match(TokenType.FALSE))
            return new Expr.Literal(previous().value);
        if(match(TokenType.TRUE))
            return new Expr.Literal(previous().value);
        if(match(TokenType.NULL))
            return new Expr.Literal(null);

        if(match(TokenType.LEFT_PAREN)){
            Expr.Grouping groupingExpression = new Expr.Grouping(expression());
            consume(TokenType.RIGHT_PAREN, "Expected ) after expression");
            return groupingExpression;
        }

        throw error(peek().lineNumber, "Expected primary value");
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
                advance();
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

    private Token previous(){
        return tokens.get(current-1);
    }

    private Token consume(TokenType type, String errorMessage){
        if(check(type))
            return advance();

        throw error(peek().lineNumber, errorMessage);
    }

    private ParserError error(int lineNumber, String errorMessage){
        Kac.error(lineNumber, errorMessage);
        return new ParserError();
    }

    private void synchronize(){
        //method used to synchronize parser after encountering error
    }
}
