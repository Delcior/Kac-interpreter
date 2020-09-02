package com.kac;

public class astPrinter implements Expr.Visitor<String>{

    public static void main(String[] args) {
        Expr expression = new Expr.Binary(
                new Expr.Unary(
                        new Token(TokenType.MINUS, "-", null, 1),
                        new Expr.Literal(123)),
                new Token(TokenType.STAR, "*", null, 1),
                new Expr.Grouping(
                        new Expr.Literal(45.67)));

        System.out.println(new astPrinter().print(expression));
    }
    String print(Expr expr) {
        return expr.accept(this);
    }
    public String paren(String name, Expr... exps){
        StringBuilder builder = new StringBuilder();

        builder.append("(").append(name);
        for (Expr expr : exps) {
            builder.append(" ");
            builder.append(expr.accept(this));
        }
        builder.append(")");

        return builder.toString();
    }
    @Override
    public String visitBinaryExpr(Expr.Binary expr) {
        return paren(expr.operator.lexeme, expr.left, expr.right);
    }

    @Override
    public String visitGroupingExpr(Expr.Grouping expr) {
        return paren("grp", expr.expression);
    }

    @Override
    public String visitLiteralExpr(Expr.Literal expr) {
        if(expr.value == null)
            return "NULL";
        return expr.value.toString();
    }

    @Override
    public String visitUnaryExpr(Expr.Unary expr) {
        return paren(expr.operator.lexeme, expr.right);
    }
}
