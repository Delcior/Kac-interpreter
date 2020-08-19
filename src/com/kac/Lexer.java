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

        if(currentCharacter == '\n'){
            lineCounter++;
            return;
        }
        //sprawdz czy jest single char
        //pozniej czy jest np == lub <=
        if(singleCharacterTokens.containsKey(currentCharacter)) {
            addToken(singleCharacterTokens.get(currentCharacter));
            return;
        }
        switch(currentCharacter){
            case '!': addToken(match('=') ? TokenType.EXCL_MARK_EQUAL : TokenType.EXCL_MARK);break;
            case '>': addToken(match('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER);break;
            case '<': addToken(match('=') ? TokenType.LESS_EQUAL : TokenType.LESS);break;
            case '=': addToken(match('=') ? TokenType.EQUAL_EQUAL : TokenType.EQUAL);break;
            case '/':
                if (match(currentCharacter)) {//double-slash czyli komentarz
                    ignoreLine();
                }else {
                    addToken(TokenType.SLASH);
                }break;
        }
    }
    private void ignoreLine(){
        char c='0';
        while(c!='\n' && !isFinishedScanning()){
            c=getCurrentChar();
        }
    }

    private void addToken(TokenType tokenType){
        addToken(tokenType, null);
    }

    private void addToken(TokenType tokenType, Object literal){
        String text = source.substring(start, currentCharacterPosition);
        tokens.add(new Token(tokenType, text, literal, lineCounter));
    }

    private boolean match(char expected){
        if(isFinishedScanning())
            return false;

        if(source.charAt(currentCharacterPosition) != expected)
            return false;

        currentCharacterPosition++;
        return true;
    }

    private char getCurrentChar(){
        return source.charAt(currentCharacterPosition++);
    }

    private boolean isFinishedScanning(){
        return currentCharacterPosition >= source.length();
    }

}
