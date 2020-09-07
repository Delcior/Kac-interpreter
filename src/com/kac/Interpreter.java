package com.kac;

public class Interpreter implements Expr.Visitor<Object>{

    private Object evaluate(Expr expr){
        return expr.accept(this);
    }

    private boolean isTrue(Object object){
        if(object == null)
            return false;
        if(object instanceof Boolean){
            return (Boolean)object;
        }
        return true;
    }

    @Override
    public Object visitBinaryExpr(Expr.Binary expr) {
        Object left = evaluate(expr);
        Object right = evaluate(expr);

        switch (expr.operator.tokenType){

        }
    }

    @Override
    public Object visitGroupingExpr(Expr.Grouping expr) {
        return evaluate(expr);
    }

    @Override
    public Object visitLiteralExpr(Expr.Literal expr) {
        return expr.value;
    }

    @Override
    public Object visitUnaryExpr(Expr.Unary expr) {
        Object right = evaluate(expr.right);

        return switch (expr.operator.tokenType) {
            //check instanceof string and throw runtime error
            case MINUS -> -(double) right;
            case EXCL_MARK -> !isTrue(right);
            default -> null;
        };
    }
}
