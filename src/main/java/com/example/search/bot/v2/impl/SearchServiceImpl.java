package main.java.com.example.search.bot.v2.impl;

import main.java.com.example.core.ClassInfo;
import main.java.com.example.core.FileReader;
import main.java.com.example.core.SearchService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SearchServiceImpl implements SearchService {

    private final FileReader fileReader;

    public SearchServiceImpl(FileReader fileReader) {
        this.fileReader = fileReader;
    }

    private static <T> T findFirstOrElse(Stream<T> stream) {
        return stream
                .filter(parentName -> parentName != null)
                .findFirst().orElse(null);
    }

    public Map<String, List<String>> buildInheritanceIndex(String projectPath, Pattern pattern) throws IOException, InterruptedException {
        List<String> files = fileReader.readFiles(projectPath);

        List<Thread> threads = new ArrayList<>();
        Map<String, List<String>> resultMap = new ConcurrentHashMap<>();

        for (String file : files) {
            Thread thread = new Thread(() -> {
                try {
                    String content = fileReader.readFileContent(file);
                    Matcher matcher = pattern.matcher(content);

                    resultMap.computeIfAbsent(
                                    findFirstOrElse(matcher.results().map(result -> result.group(2))),
                                    key -> new ArrayList<>())
                            .add(
                                    findFirstOrElse(matcher.results().map(result -> result.group(2)))
                            );

                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            thread.start();
            threads.add(thread);
        }

        return resultMap;
    }
}