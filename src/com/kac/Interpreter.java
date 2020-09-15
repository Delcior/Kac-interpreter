package com.kac;

import java.util.List;

public class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void>{

    static class RuntimeError extends RuntimeException{
        final Token token;

        RuntimeError(Token token, String message){
            super(message);
            this.token=token;
        }
    }
    private final Environment environment;

    Interpreter(){
        environment = new Environment();
    }

    public void interpret(List<Stmt> statements){
        try{
            for(Stmt stmt : statements)
                execute(stmt);
        }catch (RuntimeError exc){
            Kac.runtimeError(exc.token.lineNumber,  exc.getMessage());
        }
    }

    private String convertToString(Object value){
        if(value == null)
            return "null";

        if(value instanceof Double)
            return cutTheDecimal(value);

        return value.toString();

    }

    private Void execute(Stmt stmt){
        return stmt.accept(this);
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
        if(left instanceof String && right instanceof String)
            return (String)left + right;

        if(left instanceof Double && right instanceof Double)
            return (double)left + (double)right;

        if(left instanceof String)
            return left + cutTheDecimal(right);

        if(right instanceof String)
            return cutTheDecimal(left) + right;

        throw new RuntimeError(operator, "Bad type near '+' operator. Allowed types: [String, Number]");
    }

    private Object minusLogic(Token operator, Object left, Object right){
        if(left instanceof Double && right instanceof Double)
            return (double)left - (double)right;

        throw new RuntimeError(operator, "Bad type near '-' operator. Allowed types: [String, Number]");
    }

    private Object divisionLogic(Token operator, Object left, Object right){
        checkNumberOperands(operator, left, right);

        if((double)right == 0)
            throw new RuntimeError(operator, "Hold up, we dont do that here -> division by zer0");

        return (double)left / (double)right;

    }
    private Object greaterLogic(Token operator, Object left, Object right){
        if(left instanceof Double && right instanceof Double)
            return (double)left > (double)right;

        if(left instanceof String && right instanceof String)
            return left.toString().compareTo(right.toString()) > 0;

        throw new RuntimeError(operator, "Incompatible Types near '>' token");

    }

    private Object greaterEqualLogic(Token operator, Object left, Object right){
        if(left instanceof Double && right instanceof Double)
            return (double)left >= (double)right;

        if(left instanceof String && right instanceof String)
            return left.toString().compareTo(right.toString()) >= 0;

        throw new RuntimeError(operator, "Incompatible Types near '>=' token");
    }

    private Object lessLogic(Token operator, Object left, Object right){
        if(left instanceof Double && right instanceof Double)
            return (double)left < (double)right;

        if(left instanceof String && right instanceof String)
            return left.toString().compareTo(right.toString()) < 0;

        throw new RuntimeError(operator, "Incompatible Types near '<' token");

    }

    private Object lessEqualLogic(Token operator, Object left, Object right){
        if(left instanceof Double && right instanceof Double)
            return (double)left >= (double)right;

        if(left instanceof String && right instanceof String)
            return left.toString().compareTo(right.toString()) <= 0;

        throw new RuntimeError(operator, "Incompatible Types near '<=' token");
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

    private String cutTheDecimal(Object number){
        String stringNumber = number.toString();
        return stringNumber.substring(0, stringNumber.length()-2);
    }

    private Void assignmentLogic(Token operator, Object left, Object right){
        System.out.println(right.getClass());
        if(left instanceof Expr.Variable){
            environment.add(((Expr.Variable) left).name, right);
            return null;
        }
        throw new RuntimeError(operator, "Expected lvalue token");
    }

    @Override
    public Void visitExpressionStmt(Stmt.Expression stmt) {
        evaluate(stmt.expression);
        return null;
    }

    @Override
    public Void visitPrintStmt(Stmt.Print stmt) {
        Object value = evaluate(stmt.expression);
        System.out.println(convertToString(value));
        return null;
    }

    @Override
    public Void visitVarDeclarationStmt(Stmt.VarDeclaration stmt) {
        if(stmt.initialValue == null)
            environment.add(stmt.name, null);
        else
            environment.add(stmt.name, evaluate(stmt.initialValue));

        return null;
    }

    @Override
    public Object visitAssignmentExpr(Expr.Assignment expr) {
        Object value = evaluate(expr.value);
        environment.add(expr.variable.name, value);
        return null;
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
            case SLASH:
                return divisionLogic(expr.operator, left, right);
            case GREATER://todo: add suppoer for "string" > 3, which means if "string" is longer that 3
                return greaterLogic(expr.operator, left, right);
            case GREATER_EQUAL:
                return greaterEqualLogic(expr.operator, left, right);
            case LESS:
                return lessLogic(expr.operator, left, right);
            case LESS_EQUAL:
                return lessEqualLogic(expr.operator, left, right);
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

    @Override
    public Object visitVariableExpr(Expr.Variable var) {
        return environment.get(var.name);
    }
}
