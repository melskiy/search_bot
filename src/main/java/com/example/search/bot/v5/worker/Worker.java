package main.java.com.example.search.bot.v5.worker;

import main.java.com.example.core.ClassInfo;
import main.java.com.example.core.FileReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Worker implements Runnable {

    private final BlockingQueue<String> taskQueue;
    private final BlockingQueue<Map<String, List<String>>> resultQueue;
    private final Pattern pattern;
    private final FileReader fileReader;

    public Worker(BlockingQueue<String> taskQueue, BlockingQueue<Map<String, List<String>>> resultQueue, Pattern pattern, FileReader fileReader, ReentrantLock lock) {
        this.taskQueue = taskQueue;
        this.resultQueue = resultQueue;
        this.pattern = pattern;
        this.fileReader = fileReader;
    }

    @Override
    public void run() {
        try {
            while (true) {
                String file = taskQueue.take();
                if (file.equals("POISON_PILL")) {
                    break;
                }
                Map<String, List<String>> localIndex = new HashMap<>();
                try {
                    String content = fileReader.readFileContent(file);
                    Matcher matcher = pattern.matcher(content);
                    List<ClassInfo> classInfos = matcher.results()
                            .map(result -> new ClassInfo(result.group(2), result.group(4)))
                            .filter(info -> info.parentName() != null)
                            .toList();

                    for (ClassInfo info : classInfos) {
                        localIndex.computeIfAbsent(info.className(), k -> new ArrayList<>()).add(info.parentName());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                resultQueue.put(localIndex);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
