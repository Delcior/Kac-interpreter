package com.kac;

public class ReversePolishNotation implements Expr.Visitor<String> {

    public static void main(String[] args){
        Expr expression = new Expr.Binary(
                new Expr.Unary(
                        new Token(TokenType.MINUS, "-", null, 1),
                        new Expr.Literal(123)),
                new Token(TokenType.STAR, "*", null, 1),
                        new Expr.Literal(45.67));

        System.out.println(new ReversePolishNotation().getRPN(expression));
    }

    public String getRPN(Expr expr){
        return expr.accept(this);
    }

    @Override
    public String visitBinaryExpr(Expr.Binary expr) {
        if(expr == null)
            return "";
        String result="";

        result+=expr.left.accept(this);
        result+=" " + expr.right.accept(this);

        return result + " " + expr.operator.lexeme;
    }

    @Override
    public String visitGroupingExpr(Expr.Grouping expr) {
        if(expr == null)
            return "";
        return expr.expression.accept(this) + " ()";
    }

    @Override
    public String visitLiteralExpr(Expr.Literal expr) {
        if(expr == null)
            return "NULL";
        return expr.value.toString();
    }

    @Override
    public String visitUnaryExpr(Expr.Unary expr) {
        if(expr == null)
            return "";
        String result="";

        result+=expr.right.accept(this);

        return result + " " + expr.operator.lexeme;
    }

    @Override
    public String visitVariableExpr(Expr.Variable var) {
        return null;
    }

    @Override
    public String visitAssignmentExpr(Expr.Assignment expr) {
        return null;
    }
}
