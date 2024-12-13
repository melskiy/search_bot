package main.java.com.example.wordcounter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class FileReader {
    public String readFile(String filePath) {
        try {
            return Files.readAllLines(Paths.get(filePath))
                    .stream()
                    .collect(Collectors.joining(" "));
        } catch (IOException e) {
            System.out.println("Файл не найден: " + e.getMessage());
            return null;
        }
    }
}