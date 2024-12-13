package main.java.com.example.search.bot.v4.impl;

import main.java.com.example.core.ClassInfo;
import main.java.com.example.core.FileReader;
import main.java.com.example.core.SearchService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SearchServiceImpl implements SearchService {

    private final FileReader fileReader;
    private final Map<String, List<String>> inheritanceIndex = new ConcurrentHashMap<>();
    private final ReentrantLock lock = new ReentrantLock();

    private final ExecutorService executorService;

    public SearchServiceImpl(FileReader fileReader)  {
        this.fileReader = fileReader;
        this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    public Map<String, List<String>> buildInheritanceIndex(String projectPath, Pattern pattern) throws IOException, InterruptedException, ExecutionException {
        List<String> files = fileReader.readFiles(projectPath);

        Map<String, List<String>> inheritanceIndex = new ConcurrentHashMap<>();

        List<Future<Map<String, List<String>>>> futures = new ArrayList<>();

        for (String file : files) {
            Future<Map<String, List<String>>> future = executorService.submit(() -> {
                try {
                    String content = fileReader.readFileContent(file);
                    Matcher matcher = pattern.matcher(content);

                    Map<String, List<String>> localIndex = matcher.results()
                            .map(result -> new ClassInfo(result.group(2), result.group(4)))
                            .filter(info -> info.className() != null)
                            .collect(Collectors.groupingBy(ClassInfo::className,
                                    Collectors.mapping(ClassInfo::parentName, Collectors.toList())));

                    return localIndex;
                } catch (IOException e) {
                    e.printStackTrace();
                    return Collections.emptyMap();
                }
            });
            futures.add(future);
        }

        for (Future<Map<String, List<String>>> future : futures) {
            Map<String, List<String>> localIndex = future.get();
            if (!localIndex.isEmpty()) {
                for (Map.Entry<String, List<String>> entry : localIndex.entrySet()) {
                    inheritanceIndex.computeIfAbsent(entry.getKey(), k -> new ArrayList<>()).addAll(entry.getValue());
                }
            }
        }

        executorService.shutdown();
        return inheritanceIndex;
    }

}