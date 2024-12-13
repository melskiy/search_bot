package main.java.com.example.search.bot.v3.impl;

import main.java.com.example.core.ClassInfo;
import main.java.com.example.core.FileReader;
import main.java.com.example.core.SearchService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchServiceImpl implements SearchService {

    private final FileReader fileReader;
    private final Map<String, List<String>> inheritanceIndex = new ConcurrentHashMap<>();
    private final ReentrantLock lock = new ReentrantLock();

    public SearchServiceImpl(FileReader fileReader) {
        this.fileReader = fileReader;
    }


    public Map<String, List<String>> buildInheritanceIndex(String projectPath, Pattern pattern) throws IOException, InterruptedException {
        List<String> files = fileReader.readFiles(projectPath);

        CountDownLatch latch = new CountDownLatch(files.size());
        List<Thread> threads = new ArrayList<>();
        for (String file : files) {
            Thread thread = new Thread(() -> {
                try {
                    String content = fileReader.readFileContent(file);
                    Matcher matcher = pattern.matcher(content);
                    List<ClassInfo> classInfos;
                    classInfos = matcher.results()
                            .map(result -> new ClassInfo(result.group(2), result.group(4)))
                            .filter(info -> info.parentName() != null)
                            .toList();

                        for (ClassInfo info : classInfos) {
                            inheritanceIndex.computeIfAbsent(info.className(), k -> Collections.synchronizedList(new ArrayList<>())).add(info.parentName());
                        }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
            thread.start();
            threads.add(thread);
        }

        latch.await();

        return inheritanceIndex;
    }
}