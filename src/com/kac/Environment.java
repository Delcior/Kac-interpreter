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
}