package com.kac;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Lexer {

    private static final Map<Character, TokenType> singleCharacterTokens;
    private static final Map<String, TokenType> keywords;

    static{
        singleCharacterTokens = new HashMap<>();
        singleCharacterTokens.put(';', TokenType.SEMICOLON);
        singleCharacterTokens.put('.', TokenType.DOT);
        singleCharacterTokens.put(',', TokenType.COMMA);
        singleCharacterTokens.put('*', TokenType.STAR);
        singleCharacterTokens.put('+', TokenType.PLUS);
        singleCharacterTokens.put('-', TokenType.MINUS);
        singleCharacterTokens.put('(', TokenType.LEFT_PAREN);
        singleCharacterTokens.put(')', TokenType.RIGHT_PAREN);
        singleCharacterTokens.put('{', TokenType.LEFT_BRACE);
        singleCharacterTokens.put('}', TokenType.RIGHT_BRACE);
        //charToToken.put('/', TokenType.SLASH);
        //charToToken.put('!', TokenType.EXCL_MARK);

        keywords = new HashMap<>();
        keywords.put("if",      TokenType.IF);
        keywords.put("else",    TokenType.ELSE);
        keywords.put("while",   TokenType.WHILE);
        keywords.put("and",     TokenType.AND);
        keywords.put("or",      TokenType.OR);
        keywords.put("for",     TokenType.FOR);
        keywords.put("true",    TokenType.TRUE);
        keywords.put("false",   TokenType.FALSE);
        keywords.put("return",  TokenType.RETURN);
        keywords.put("print",   TokenType.PRINT);
        keywords.put("super",   TokenType.SUPER);
        keywords.put("this",    TokenType.THIS);
        keywords.put("null",    TokenType.NULL);
        keywords.put("string",  TokenType.STRING);
    }

    private List<Token> tokens;
    private String source;
    //tracking lines/scanned characters
    private int start;
    private int currentCharacterPosition;
    private int lineCounter;
    private int length;

    public Lexer(String source){
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
            start=currentCharacterPosition;
            scanToken();
        }
        tokens.add(new Token(TokenType.EOF,"", null, lineCounter));
        return tokens;
    }
    private void scanToken(){
        char currentCharacter = getCurrentChar();
        //sprawdz czy jest single char
        //pozniej czy jest np == lub <=
        if(singleCharacterTokens.containsKey(currentCharacter))
            addToken(singleCharacterTokens.get(currentCharacter));

    }

    private void addToken(TokenType tokenType){
        String text = source.substring(start, currentCharacterPosition);
        tokens.add(new Token(tokenType, text, null, lineCounter));
    }
    private char getCurrentChar(){
        return source.charAt(currentCharacterPosition++);
    }
    private boolean isFinishedScanning(){
        return currentCharacterPosition >= source.length();
    }

}
