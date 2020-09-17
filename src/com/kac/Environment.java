package com.kac;

import java.util.HashMap;
import java.util.Map;

public class Environment {
    final Environment outerEnv;
    Map<String, Object> values = new HashMap<>();

    Environment(){
        outerEnv = null;
    }

    Environment(Environment outerEnv){
        this.outerEnv = outerEnv;
    }

    public void declare(Token name, Object value){
        if(values.containsKey(name.lexeme))
            throw new Interpreter.RuntimeError(name, "Variable '" + name.lexeme +"'" +
                    "is already declared");
        values.put(name.lexeme, value);
    }

    public void assign(Token name, Object value){
        if(values.containsKey(name.lexeme))
            values.put(name.lexeme, value);
        assignInOuterEnv(outerEnv, name, value);
    }

    private Void assignInOuterEnv(Environment outerEnv, Token name, Object value){
        if(outerEnv != null){
            if(outerEnv.values.containsKey(name.lexeme)) {
                outerEnv.values.put(name.lexeme, value);
                return null;
            }
            return assignInOuterEnv(outerEnv.outerEnv, name, value);
        }
        throw new Interpreter.RuntimeError(name, "Variable '" + name.lexeme +"' is not declared");
    }
    public Object get(Token name){
        Object value = values.get(name.lexeme);

        if(value == null){
            value = checkOuterEnv(outerEnv, name);
            if(value == null)
                throw new Interpreter.RuntimeError(name, "Variable '" + name.lexeme +"' is not declared");
        }
        return value;
    }

    private Object checkOuterEnv(Environment outerEnv, Token name){
        if(outerEnv == null)
            return null;

        Object value = outerEnv.values.get(name.lexeme);
        if(value == null)
            return checkOuterEnv(outerEnv.outerEnv, name);
        return value;
    }
}