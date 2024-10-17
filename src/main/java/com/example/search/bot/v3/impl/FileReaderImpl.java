package main.java.com.example.search.bot.v3.impl;

import main.java.com.example.core.FileReader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class FileReaderImpl implements FileReader {

    @Override
    public List<String> readFiles(String projectPath) throws IOException  {
        List<String> files = null;
        try (var paths = Files.walk(Paths.get(projectPath))) {
            files = paths
                    .filter(Files::isRegularFile)
                    .map(Path::toString)
                    .filter(string -> string.endsWith(".java"))
                    .toList();
        } catch (IOException e) {
            System.out.println("Ошибка ввода-вывода: " + e.getMessage());
        }
        return files;
    }

    @Override
    public String readFileContent(String filePath) throws IOException {
        return Files.readString(Paths.get(filePath));
    }

}
