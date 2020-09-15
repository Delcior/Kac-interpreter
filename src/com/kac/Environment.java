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
        values.put(name.lexeme, value);
    }
    public Object get(Token name){
        Object value = values.get(name.lexeme);

        if(value == null)
            throw new Interpreter.RuntimeError(name, "No such variable declared: " + name.lexeme);
        return value;
    }
}