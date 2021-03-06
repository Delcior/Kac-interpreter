package com.kac;

import java.util.HashMap;
import java.util.List;

public class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void>{
    //TODO: make Environment interface - needed for class,also does functions belong to scope?
    //TODO: var declaration inside for statement makes variable global
    static class RuntimeError extends RuntimeException{
        final Token token;

        RuntimeError(Token token, String message){
            super(message);
            this.token=token;
        }
    }

    static class Return extends RuntimeException{
        final Object value;

        Return(Object value){
            this.value=value;
        }
        Object getValue(){return value;}
    }

    private final HashMap<String, Class> classes;
    private HashMap<String, Stmt.FunctionDeclaration> functions;
    private Environment environment;

    Interpreter(){
        environment = new Environment();
        functions = new HashMap<>();
        classes = new HashMap<>();
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
    //fun foo(n){ if(n>0) foo(n-1); print n;} foo(1);
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
            environment.declare(stmt.name, null);
        else
            environment.declare(stmt.name, evaluate(stmt.initialValue));

        return null;
    }

    @Override
    public Void visitScopeStmt(Stmt.Scope stmt) {
        Environment current_scope = environment;
        environment = new Environment(environment);

        for(Stmt statement : stmt.statements)
            statement.accept(this);

        environment = current_scope;
        return null;
    }

    @Override
    public Void visitIfStmt(Stmt.If stmt) {
        //todo: add condition operator
        Object conditionValue = evaluate(stmt.condition);

        if(isTrue(conditionValue)){
            execute(stmt.ifBranch);
        }else{
            if(stmt.elseBranch != null)
                execute(stmt.elseBranch);
        }
        return null;
    }

    @Override
    public Void visitWhileStmt(Stmt.While stmt) {
        while(isTrue(evaluate(stmt.condition))){
            execute(stmt.body);
        }
        return null;
    }

    @Override
    public Void visitForStmt(Stmt.For stmt) {
        Environment tmp_env = environment;
        if(isTrue(stmt.initializer)) {
            environment = new Environment(tmp_env);
            execute(stmt.initializer);
        }

        while(isTrue(evaluate(stmt.condition))){
            execute(stmt.body);
            execute(stmt.stateModifier);
        }
        environment = tmp_env;
        return null;
    }

    @Override
    public Void visitFunctionDeclarationStmt(Stmt.FunctionDeclaration stmt) {
        //Callable fun = new Callable(stmt.arguments, stmt.body);
        functions.put(stmt.name.lexeme, stmt);

        return null;
    }

    @Override
    public Void visitClassDeclarationStmt(Stmt.ClassDeclaration stmt) {
        Environment tmp_env = environment;
        HashMap<String, Stmt.FunctionDeclaration> tmp_fun = functions;

        environment = new Environment();
        functions = new HashMap<>();

        for(Stmt statement : stmt.classData)
            statement.accept(this);

        Class new_class = new Class();
        new_class.publicFunctions = functions;
        new_class.publicVariables = environment;
        new_class.name = stmt.name.lexeme;

        environment = tmp_env;
        functions = tmp_fun;
        return null;
    }

    @Override
    public Void visitReturnStmt(Stmt.Return stmt) {
        Object value = evaluate(stmt.value);

        throw new Return(value);
    }

    @Override
    public Object visitAssignmentExpr(Expr.Assignment expr) {
        Object value = evaluate(expr.value);
        environment.assign(expr.variable.name, value);
        return null;
    }
    //todo: consider returning actual objects instead of boolean value
    @Override
    public Object visitLogicalOrExpr(Expr.LogicalOR expr) {
        boolean resultLeft = isTrue(evaluate(expr.left));

        if(resultLeft)
            return true;

        return isTrue(evaluate(expr.right));
    }

    @Override
    public Object visitLogicalAndExpr(Expr.LogicalAND expr) {
        boolean left = isTrue(evaluate(expr.left));
        boolean right = isTrue(evaluate(expr.right));

        return left && right;
    }

    @Override
    public Object visitFunctionCall(Expr.FunctionCall expr) {
        //TODO: null fun check
        //TODO: create Callable class
        //todo: create object initialization function;
        //function call
        Object retValue = null;
        Environment new_fun_scope = new Environment(environment.getGlobalEnv());
        Environment current_scope = environment;
        Stmt.FunctionDeclaration fun = functions.get(expr.name.lexeme);

        for(int i=0; i<expr.args.size(); i++)
            new_fun_scope.declare(fun.arguments.get(i), evaluate(expr.args.get(i)));

        environment = new_fun_scope;
        try{
            execute(fun.body);
        }catch (Return ret){
            retValue = ret.getValue();
        }
        environment = current_scope;
        return retValue;
    }

    @Override
    public Object visitBinaryExpr(Expr.Binary expr) {
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);

        switch (expr.operator.tokenType){
            case COMMA:
                return right;
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
