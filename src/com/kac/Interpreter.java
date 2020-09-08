package com.kac;

public class Interpreter implements Expr.Visitor<Object>{

    private class RuntimeError extends RuntimeException{
        final Token token;

        RuntimeError(Token token, String message){
            super(message);
            this.token=token;
        }
    }
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

    private Object plusLogic(Token operator, Object left, Object right){
        //3 cases: int + int, double + double/int, string + string
        //add support for more number types
        if(left instanceof String && right instanceof String)
            return (String)left + right;
//        if(left instanceof Integer && right instanceof Integer)
//            return (int)left + (int)right;
        if(left instanceof Double && right instanceof Double)
            return (double)left + (double)right;

        throw new RuntimeError(operator, "Allowed '+' operations on types: string + string, (double | int) + (double | int)");
    }

    private Object minusLogic(Token operator, Object left, Object right){
//        if(left instanceof Integer && right instanceof Integer)
//            return (int)left - (int)right;
        if(left instanceof Double && right instanceof Double)
            return (double)left - (double)right;

        throw new RuntimeError(operator, "Allowed '-' operations on types:(double | int) + (double | int)");
    }

    private void checkNumberOperands(Token operator, Object left, Object right){
        if(left instanceof Double && right instanceof Double)
            return;

        throw new RuntimeError(operator, "Operand must be a number!");
    }

    private boolean isEqual(Object left, Object right){
        if(left == null && right == null)
            return true;
        if(left == null)
            return false;

        return left.equals(right);
    }

    @Override
    public Object visitBinaryExpr(Expr.Binary expr) {
        Object left = evaluate(expr);
        Object right = evaluate(expr);

        switch (expr.operator.tokenType){
            case PLUS:
                return plusLogic(expr.operator, left, right);
            case MINUS:
                return minusLogic(expr.operator, left, right);
            case STAR:
                checkNumberOperands(expr.operator, left, right);
                return (double)left * (double)right;
            case SLASH://todo: check if right is not zero
                checkNumberOperands(expr.operator, left, right);
                return (double)left / (double)right;
            case GREATER:
                checkNumberOperands(expr.operator, left, right);
                return (double)left > (double)right;
            case GREATER_EQUAL:
                checkNumberOperands(expr.operator, left, right);
                return (double)left >= (double)right;
            case LESS:
                checkNumberOperands(expr.operator, left, right);
                return (double)left < (double)right;
            case LESS_EQUAL:
                checkNumberOperands(expr.operator, left, right);
                return (double)left <= (double)right;
            case EQUAL_EQUAL:
                return isEqual(left, right);
            case EXCL_MARK_EQUAL:
                return !isEqual(left, right);
        }
        return null;
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
