package com.kac;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Kac {
    private static boolean hadError=false;

    public static void main(String[] args) throws IOException {
        if(args.length == 0){
            runConsole();
        }else if(args.length == 1){
            runFile(args[0]);
        }
        runConsole();

    }

    private static void runFile(String pathToFile) throws IOException{
        byte[] bytes = Files.readAllBytes(Paths.get(pathToFile));
        run(new String(bytes, Charset.defaultCharset()));
    }

    private static void runConsole() throws IOException{
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        for (;;) {
            System.out.print("kac> ");
            String line = reader.readLine();
            if (line == null) break;
            run(line);
        }
    }

    private static void run(String source) {
        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.scanTokens();

        if(hadError){
            hadError=false;
            return;
        }
        // For now just print the tokens.
        for (Token token : tokens) {
            System.out.println(token);
        }
    }

    static void error(int line, String errorMessage){
        System.err.println("Error occurred in line: " + line + ". Message: " + errorMessage);
        hadError=true;
    }
}
