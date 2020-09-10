package com.kac;

public class Interpreter implements Expr.Visitor<Object>{

    private static class RuntimeError extends RuntimeException{
        final Token token;

        RuntimeError(Token token, String message){
            super(message);
            this.token=token;
        }
    }

    public String interpret(Expr expr){
        try{
            Object result = evaluate(expr);
            return convertToString(result);
        }catch (RuntimeError exc){
            Kac.runtimeError(exc.token.lineNumber, "RuntimeError !");
            return null;
        }
    }

    private String convertToString(Object value){
        if(value == null)
            return "null";

        if(value instanceof Number){
            String stringValue = value.toString();
            if(stringValue.endsWith(".0"))
                return stringValue.substring(0, stringValue.length()-2);
            return stringValue;
        }

        return value.toString();

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
        if(left instanceof Number && right instanceof Number)
            return (double)left + (double)right;

        throw new RuntimeError(operator, "Allowed '+' operations on types: string + string, (double | int) + (double | int)");
    }

    private Object minusLogic(Token operator, Object left, Object right){
//        if(left instanceof Integer && right instanceof Integer)
//            return (int)left - (int)right;
        if(left instanceof Number && right instanceof Number)
            return (double)left - (double)right;

        throw new RuntimeError(operator, "Allowed '-' operations on types:(double | int) + (double | int)");
    }

    private Object greaterLogic(Token operator, Object left, Object right){
        if(left instanceof Number && right instanceof Number)
            return (double)left > (double)right;

        if(left instanceof String && right instanceof String)
            return left.toString().compareTo(right.toString()) > 0;

        throw new RuntimeError(operator, "Incompatible Types near '>' token");

    }

    private Object greaterEqualLogic(Token operator, Object left, Object right){
        if(left instanceof Number && right instanceof Number)
            return (double)left >= (double)right;

        if(left instanceof String && right instanceof String)
            return left.toString().compareTo(right.toString()) >= 0;

        throw new RuntimeError(operator, "Incompatible Types near '>=' token");
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
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);

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
                return greaterLogic(expr.operator, left, right);
            case GREATER_EQUAL:
                return greaterEqualLogic(expr.operator, left, right);
            case LESS:
                return !(boolean)greaterEqualLogic(expr.operator, left, right);
            case LESS_EQUAL:
                return !(boolean)greaterLogic(expr.operator, left, right);
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
            //todo:check instanceof string and throw runtime error
            case MINUS -> -(double) right;
            case EXCL_MARK -> !isTrue(right);
            default -> null;
        };
    }
}
