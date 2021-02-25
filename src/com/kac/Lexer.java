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
        singleCharacterTokens.put('+', TokenType.PLUS);
        singleCharacterTokens.put('-', TokenType.MINUS);
        singleCharacterTokens.put('(', TokenType.LEFT_PAREN);
        singleCharacterTokens.put(')', TokenType.RIGHT_PAREN);
        singleCharacterTokens.put('{', TokenType.LEFT_BRACE);
        singleCharacterTokens.put('}', TokenType.RIGHT_BRACE);

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
        keywords.put("super",   TokenType.SUPER);
        keywords.put("print",   TokenType.PRINT);
        keywords.put("var",     TokenType.VAR);
        keywords.put("this",    TokenType.THIS);
        keywords.put("null",    TokenType.NULL);
        keywords.put("string",  TokenType.STRING);
        keywords.put("fun",  TokenType.FUN);
    }

    private final List<Token> tokens;
    private final String source;
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
        char currentCharacter = advance();

        if(singleCharacterTokens.containsKey(currentCharacter)) {
            addToken(singleCharacterTokens.get(currentCharacter));
            return;
        }
        //znaki typu >=, == itp.
        switch(currentCharacter){
            case '!': addToken(match('=') ? TokenType.EXCL_MARK_EQUAL : TokenType.EXCL_MARK);break;
            case '>': addToken(match('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER);break;
            case '<': addToken(match('=') ? TokenType.LESS_EQUAL : TokenType.LESS);break;
            case '=': addToken(match('=') ? TokenType.EQUAL_EQUAL : TokenType.EQUAL);break;
            case '*': addToken(TokenType.STAR);break;
            case '/':
                if (match(currentCharacter)) {//double-slash czyli komentarz
                    ignoreLine();
                }else if(match('*')){
                    comment();
                }else {
                    addToken(TokenType.SLASH);
                }break;
            case ' ':
            case '\r':
            case '\t':
                break;
            case '\n': lineCounter++; break;
            case '"': readString(); break;
            default:
                if(isDigit(currentCharacter)){
                    readNumber();
                }else if(isAlphaNumeric(currentCharacter)){
                    readIndentifier();
                }else
                    Kac.error(lineCounter, "Unexpected character.");

        }
    }

    private void ignoreLine(){
        char c='0';
        while(c!='\n' && !isFinishedScanning()){
            c= advance();
        }
    }

    private void comment(){
        while(!isFinishedScanning()){
            if(peek() == '*' && peekNext() == '/'){
                advance();advance();
                break;
            }
            advance();
        }
    }

    private boolean isDigit(char c){
        return c>='0' && c<='9';
    }

    private void readNumber(){
        while(isDigit(peek())){
            advance();
        }

        if(peek() == '.' && isDigit(peekNext())) {
            //taking "."
            advance();
        }

        while(isDigit(peek())){
            advance();
        }

        addToken(TokenType.NUMBER, Double.parseDouble(source.substring(start, currentCharacterPosition)));
    }

    private boolean isAlpha(char c){
        return  c >= 'a' && c <= 'z' ||
                c >= 'A' && c <= 'Z' ||
                c == '_';
    }

    private boolean isAlphaNumeric(char c){
        return isAlpha(c) || isDigit(c);
    }

    private void addToken(TokenType tokenType){
        addToken(tokenType, null);
    }

    private void addToken(TokenType tokenType, Object value){
        String text = source.substring(start, currentCharacterPosition);
        tokens.add(new Token(tokenType, text, value, lineCounter));
    }

    private boolean match(char expected){
        if(isFinishedScanning())
            return false;

        if(source.charAt(currentCharacterPosition) != expected)
            return false;

        currentCharacterPosition++;
        return true;
    }
    //todo: add null support ( var a=null;)
    //todo: consider storing booleans with actual value
    private void readIndentifier(){
        while (isAlphaNumeric(peek()))
            advance();

        String lexeme = source.substring(start, currentCharacterPosition).toLowerCase();

        TokenType type = keywords.get(lexeme);

        if(type == null)
            addToken(TokenType.USER_DEFINED);
        else
            addToken(type);
    }

    private void readString(){
        while(!isFinishedScanning() && peek() != '"'){
            if(peek() == '\n')
                lineCounter++;
            advance();
        }
        if(!(peek() == '"')){
            Kac.error(lineCounter, "unterminated string");
            return;
        }
        advance();
        addToken(TokenType.STRING, source.substring(start+1, currentCharacterPosition-1));
    }

    private char advance(){
        return source.charAt(currentCharacterPosition++);
    }

    private char peek(){
        if(isFinishedScanning())
            return '\0';
        return source.charAt(currentCharacterPosition);
    }

    private char peekNext(){
        if(currentCharacterPosition +1 >= length)
            return '\0';
        return source.charAt(currentCharacterPosition+1);
    }
    private boolean isFinishedScanning(){
        return currentCharacterPosition >= source.length();
    }

}
