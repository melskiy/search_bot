package main.java.com.example.search.bot.v2;
import main.java.com.example.core.FileReader;
import main.java.com.example.search.bot.v2.impl.FileReaderImpl;
import main.java.com.example.search.bot.v2.impl.SearchServiceImpl;
import main.java.com.example.utils.EnvReader;

import java.io.IOException;
import java.util.regex.Pattern;


public class SearchBot {
    public static void main(String[] args) throws IOException {
        String projectPath = new EnvReader().getPath();
        Pattern pattern = Pattern.compile("(class|interface)\\s+(\\w+)\\s*(extends|implements)?\\s*(\\w+)?");

        FileReader fileReader = new FileReaderImpl();
        SearchServiceImpl searchService = new SearchServiceImpl(fileReader);

        try {
            var inheritanceIndex = searchService.buildInheritanceIndex(projectPath, pattern);
            inheritanceIndex.forEach((parent, children) -> {
                System.out.println(parent + ": " + children);
            });
        } catch (IOException e) {
            System.out.println("Ошибка при чтении файлов: " + e.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}