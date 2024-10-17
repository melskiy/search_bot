package main.java.com.example.search.bot.v2.impl;

import main.java.com.example.core.ClassInfo;
import main.java.com.example.core.FileReader;
import main.java.com.example.core.SearchService;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SearchServiceImpl implements SearchService {

    private final FileReader fileReader;
    private final ExecutorService executorService;

    public SearchServiceImpl(FileReader fileReader) {
        this.fileReader = fileReader;
        this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    public Map<String, List<String>> buildInheritanceIndex(String projectPath, Pattern pattern) throws IOException, InterruptedException {
        List<String> files = fileReader.readFiles(projectPath);

        CountDownLatch latch = new CountDownLatch(files.size());

        List<List<ClassInfo>> results = new CopyOnWriteArrayList<>();

        for (String file : files) {
            executorService.submit(() -> {
                try {
                    String content = fileReader.readFileContent(file);
                    Matcher matcher = pattern.matcher(content);
                    results.add(matcher.results()
                            .map(result -> new ClassInfo(result.group(2), result.group(4)))
                            .filter(info -> info.parentName() != null)
                            .toList());
                } catch (IOException e) {

                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        return results.stream()
                .flatMap(List::stream)
                .collect(Collectors.groupingBy(ClassInfo::className, Collectors.mapping(ClassInfo::parentName, Collectors.toList())));
    }
}