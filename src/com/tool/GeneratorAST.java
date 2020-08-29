package com.tool;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

public class GeneratorAST {
    public static void main(String[] args){
        if(args.length != 1){
            System.out.println("This program is used to generate AST");
            System.exit(64);
        }

        String outputDirectory = args[0];
        try {
            generateAST(outputDirectory, "Expr", Arrays.asList(
                "Binary   : Expr left, Token operator, Expr right",
                "Grouping : Expr expression",
                "Literal  : Object value",
                "Unary    : Token operator, Expr right"
            ));
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    private static void generateAST(String outputDir, String baseClassName, List<String> types)
        throws IOException {
        String path = outputDir + '/' + baseClassName + ".java";
        PrintWriter writer = new PrintWriter(path, "UTF-8");

        writer.println("package com.kac;\n");
        writer.println("import java.util.List;\n");
        writer.println("abstract class " + baseClassName + " {");

        generateVisitorInterface(writer, baseClassName, types);
        for(String type : types){
            String typeName = type.split(":")[0].trim();
            String fields = type.split(":")[1].trim();
            generateType(writer, baseClassName, typeName, fields);
        }
        writer.println("\n\tabstract <T> T accept(Visitor<T> visitor);");
        writer.println("}");
        writer.close();
    }
    private static void generateVisitorInterface(PrintWriter writer, String baseName, List<String> types){
        //interface for visitor pattern
        writer.println("\tinterface Visitor<T> {");

        for (String type : types) {
            String typeName = type.split(":")[0].trim();
            writer.println("\t\tT visit" + typeName + baseName + "(" +
                    typeName + " " + baseName.toLowerCase() + ");");
        }
        writer.println("\t}");
    }
    private static void generateType(PrintWriter writer, String outerClassName, String innerClassName, String fields){
        writer.println("    static class " + innerClassName + " extends " + outerClassName + "{");
        //fields
        String[] arrOfFields = fields.split(",");
        for(String field : arrOfFields){
            String typeOfField = field.trim().split(" ")[0];
            String nameOfField = field.trim().split(" ")[1];
            writer.println("        final " + typeOfField + " " + nameOfField + ";");
        }
        //constructor
        writer.println("\n\t\t" + innerClassName + "(" + fields + ") {");
        for(String field : arrOfFields){
            String nameOfField = field.trim().split(" ")[1];
            writer.println("\t\t\tthis." + nameOfField + " = " + nameOfField + ";");
        }
        writer.println("\t\t}");
        writer.println("\n\t\t@Override");
        writer.println("\t\t<T> T accept(Visitor<T> visitor) {");
        writer.println("\t\t\treturn visitor.visit"+innerClassName + outerClassName + "(this);");
        writer.println("\t\t}");
        writer.println("\t}");
    }
}
