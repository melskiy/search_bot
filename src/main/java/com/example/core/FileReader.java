package main.java.com.example.core;

import java.io.IOException;
import java.util.List;

public interface FileReader {
    List<String> readFiles(String projectPath) throws IOException;

    String readFileContent(String file)  throws IOException ;

}
