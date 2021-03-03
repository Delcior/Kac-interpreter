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
        int begIndex;
        StringBuilder builder = new StringBuilder(source);
        StringBuilder path = new StringBuilder();
        String content = "";

        while((begIndex=builder.indexOf("#import")) != -1){
            int endIndex = builder.indexOf(";", begIndex);
            String libToBeImported = builder.substring(begIndex+7, endIndex).trim();
            builder.delete(begIndex, endIndex+1);

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
                builder.insert(begIndex, content);
            }
        }
        source=builder.toString();
    }
}
