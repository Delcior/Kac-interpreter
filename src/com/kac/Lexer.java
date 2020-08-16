package com.kac;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Lexer {

    private final HashMap<Character, TokenType> unaryTokens;

    private String source;
    private List<Token> tokens;
    //tracking lines/scanned characters
    private int start;
    private int currentCharacterPosition;
    private int lineCounter;
    private int length;

    public Lexer(String source){
        this.unaryTokens = new HashMap<>();
        this.tokens = new ArrayList<>();

        this.start = 0;
        this.currentCharacterPosition = 0;
        this.lineCounter = 1;
        this.source = source;
        this.length = source.length();
    }

    public List<Token> scanTokens(){
        while(!isFinishedScanning()){
            //reading source

        }
        tokens.add(new Token(TokenType.EOF,"", null, lineCounter));
        return tokens;
    }

    private boolean isFinishedScanning(){
        return currentCharacterPosition >= source.length();
    }
}
