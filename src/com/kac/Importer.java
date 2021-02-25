package com.kac;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public class Importer {

    private final Set<String> importedLibs;
    private String source;

    public Importer(String source){
        this.importedLibs = new HashSet<String>();
        this.source = source;
    }

    public String getSource(){
        scanImport();
        return source;
    }

    private void scanImport(){
        int index;
        StringBuilder builder = new StringBuilder(source);
        StringBuilder path = new StringBuilder();
        String content = "";
        //TODO: check if file is not included more than once
        while((index=builder.indexOf("#import")) != -1){
            int endOfInclude = builder.indexOf(";", index);
            String libToBeImported = builder.substring(index+7, endOfInclude).trim();
            builder.delete(index, endOfInclude+1);

            if(!importedLibs.contains(libToBeImported)) {
                path.append(System.getProperty("user.dir"));
                path.append("/lib/");
                path.append(libToBeImported);

                try {
                    content = Files.readString(Paths.get(path.toString()), StandardCharsets.UTF_8);
                    importedLibs.add(libToBeImported);
                } catch (IOException aa) {
                    System.out.println(aa.getMessage());
                }
                builder.insert(index, content);
            }
        }
        source=builder.toString();
        System.out.println(source);
    }
}
