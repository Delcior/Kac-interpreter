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

    public void add(Token name, Object value){
        if(values.containsKey(name.lexeme))
            values.put(name.lexeme, value);
    }
    //todo: make different methods for add and for declare variable
    private void addInOuterEnv(Environment outerEnv, Token name, Object value){
        

    }
    public Object get(Token name){
        Object value = values.get(name.lexeme);

        if(value == null){
            value = checkOuterEnv(outerEnv, name);
            if(value == null)
                throw new Interpreter.RuntimeError(name, "No such variable declared: " + name.lexeme);
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