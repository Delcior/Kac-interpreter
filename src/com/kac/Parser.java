package com.kac;

import java.util.LinkedList;
import java.util.List;

public class Parser {
    private static class ParserError extends RuntimeException {}
    //list of tokens used to create abstract syntax tree
    private final List<Token> tokens;
    private int current = 0;

    public Parser(List<Token> tokens){
        this.tokens = tokens;
    }

    public List<Stmt> parse(){
        List<Stmt> statements = new LinkedList<>();

        while(!isAtEnd()) {
            statements.add(declaration());
        }

        return statements;
    }
    //methods for Stmt class
    private Stmt declaration(){
        //todo: try catch with synchronize()
        try {
            if (match(TokenType.VAR)) {
                return varDeclaration();
            }
            return statement();
        }catch(ParserError e){
            synchronize();
            return null;
        }
    }

    private Stmt varDeclaration(){
        Token name = consume(TokenType.USER_DEFINED, "Expected user defined variable");
        Expr initialValue = null;

        if(match(TokenType.EQUAL))
            initialValue = expression();

        consume(TokenType.SEMICOLON, "Expected ; after statement.");
        return new Stmt.VarDeclaration(name, initialValue);
    }

    private Stmt statement(){
        if (match(TokenType.PRINT)) {
            return printStatement();
        }

        if(match(TokenType.LEFT_BRACE))
            return scopeStatement();

        if(match(TokenType.IF))
            return ifStatement();

        if(match(TokenType.WHILE))
            return whileStatement();

        if(match(TokenType.FOR))
            return forStatement();
        return expressionStatement();
    }

    private Stmt whileStatement(){
        consume(TokenType.LEFT_PAREN, "Expected ( before 'while' condition");
        Expr condition = expression();
        consume(TokenType.RIGHT_PAREN, "Expected ) after 'while' condition");
        Stmt statement = statement();

        return new Stmt.While(condition, statement);
    }

    private Stmt forStatement(){
        Stmt initializer = null;
        Expr condition = null;
        Expr stateModifier = null;
        Stmt body = null;

        consume(TokenType.LEFT_PAREN, "Expected ( before 'for' condition");
        if(!match(TokenType.SEMICOLON)) {
            if(match(TokenType.VAR))
                initializer = varDeclaration();
            else
                initializer = statement();
        }else
            consume(TokenType.SEMICOLON, "Expected ; after 'for' initializer");
    

        return null;
    }
    private Stmt ifStatement(){
        Expr condition;
        Stmt ifBranch;
        Stmt elseBranch = null;

        consume(TokenType.LEFT_PAREN, "Expected ( before 'if' condition");
        condition = expression();
        consume(TokenType.RIGHT_PAREN, "Expected ) after 'if' condition");

        ifBranch = statement();
        if(match(TokenType.ELSE))
            elseBranch = statement();

        return new Stmt.If(condition, ifBranch, elseBranch);
    }
    private Stmt expressionStatement(){
        Expr expression = expression();
        consume(TokenType.SEMICOLON, "Expected ; after expression");
        return new Stmt.Expression(expression);
    }

    private Stmt printStatement(){

        Expr expression = expression();
        consume(TokenType.SEMICOLON, "Expected ; after value");
        return new Stmt.Print(expression);
    }

    private Stmt scopeStatement(){
        List<Stmt> statements = new LinkedList<>();

        while(!isAtEnd()){
            if(match(TokenType.RIGHT_BRACE))
                return new Stmt.Scope(statements);
            statements.add(declaration());
        }

        throw error(peek().lineNumber, "Missing } token");
    }
    //methods for Expr class [in order of precendence level(from lowest to highest)]
    private Expr expression(){
        return comma();
    }

    private Expr comma(){
        Expr expression = assignment();

        while(match(TokenType.COMMA)){
            Token operator = previous();
            Expr equalityRight = assignment();
            expression = new Expr.Binary(expression, operator, equalityRight);
        }

        return expression;
    }

    private Expr assignment(){
        Expr expression = logicalOr();
        //todo:consider not throwing error
        while(match(TokenType.EQUAL)){
            if(!(expression instanceof Expr.Variable))
                throw error(peek().lineNumber, "Can't assign value to an r-value expression.");

            Expr right = logicalOr();
            expression = new Expr.Assignment((Expr.Variable)expression, right);
        }

        return expression;
    }
    private Expr logicalOr(){
        Expr expression = logicAND();

        if(match(TokenType.OR)){
            Expr right = logicAND();
            return new Expr.LogicalOR(expression, right);
        }
        return expression;
    }
    private Expr logicAND(){
        Expr expression = equality();

        if(match(TokenType.AND)){
            Expr right = equality();
            return new Expr.LogicalAND(expression, right);
        }
        return expression;
    }
    //TODO: add ternary operator support
    //TODO: add error production for binary expressions
    private Expr equality(){
        Expr expression = comparison();

        while(match(TokenType.EXCL_MARK_EQUAL, TokenType.EQUAL_EQUAL)){
            Token operator = previous();
            Expr comparisonRight = comparison();
            expression = new Expr.Binary(expression, operator, comparisonRight);
        }

        return expression;
    }

    private Expr comparison(){
        Expr expression = addition();

        while(match(TokenType.GREATER, TokenType.GREATER_EQUAL, TokenType.LESS, TokenType.LESS_EQUAL)){
            Token operator = previous();
            Expr additionRight = addition();
            expression = new Expr.Binary(expression, operator, additionRight);
        }

        return expression;
    }

    private Expr addition(){
        Expr expression = multiplication();

        while(match(TokenType.MINUS, TokenType.PLUS)){
            Token operator = previous();
            Expr multiplicationRight = multiplication();
            expression = new Expr.Binary(expression, operator, multiplicationRight);
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
        if(match(TokenType.USER_DEFINED))
            return new Expr.Variable(previous());

        if(match(TokenType.NUMBER, TokenType.STRING))
            return new Expr.Literal(previous().value);

        if(match(TokenType.FALSE))
            return new Expr.Literal(false);
        if(match(TokenType.TRUE))
            return new Expr.Literal(true);
        if(match(TokenType.NULL))
            return new Expr.Literal(null);

        if(match(TokenType.LEFT_PAREN)){
            Expr.Grouping groupingExpression = new Expr.Grouping(expression());
            consume(TokenType.RIGHT_PAREN, "Expected ) after expression");
            return groupingExpression;
        }
        throw error(peek().lineNumber, "Expected primary value, instead got " + peek().lexeme);
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
        //some errors do not require unwinding so throwing is not always necessary
        return new ParserError();
    }

    private void synchronize(){
        //method used to synchronize parser after encountering error

        while(!isAtEnd()){
            if(peek().tokenType == TokenType.SEMICOLON) {
                advance();
                return;
            }
            advance();
        }
    }
}
