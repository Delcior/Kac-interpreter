package com.kac;

abstract class Expr123 {

    static class Literal extends Expr123 {
        final Object value;

        Literal(Object value){
            this.value = value;
        }
    }

    static class Grouping extends Expr123 {
        final Expr123 expression;

        Grouping(Expr123 expression){
            this.expression = expression;
        }
    }

    static class Unary extends Expr123 {
        final Token operator;
        final Expr123 expression;

        Unary(Token operator, Expr123 expression){
            this.operator = operator;
            this.expression = expression;
        }
    }

    static class Binary extends Expr123 {

        final Expr123 left;
        final Expr123 right;
        final Token operator;

        Binary(Expr123 left, Token operator, Expr123 right){
            this.left = left;
            this.right = right;
            this.operator = operator;
        }
    }
}
