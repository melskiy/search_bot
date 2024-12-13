package main.java.com.example.search.bot.v5.impl;

import main.java.com.example.core.FileReader;
import main.java.com.example.core.SearchService;
import main.java.com.example.search.bot.v5.worker.Worker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;

public class SearchServiceImpl implements SearchService {

    private final FileReader fileReader;
    private final ExecutorService executorService;
    private final BlockingQueue<String> taskQueue = new LinkedBlockingQueue<>();
    private final BlockingQueue<Map<String, List<String>>> resultQueue = new LinkedBlockingQueue<>();
    private final Map<String, List<String>> inheritanceIndex = new ConcurrentHashMap<>();
    private final ReentrantLock lock = new ReentrantLock();
    private final int numWorkers;

    public SearchServiceImpl(FileReader fileReader, int numWorkers) {
        this.fileReader = fileReader;
        this.executorService = Executors.newFixedThreadPool(numWorkers);
        this.numWorkers = numWorkers;
    }

    public Map<String, List<String>> buildInheritanceIndex(String projectPath, Pattern pattern) throws IOException, InterruptedException {
        List<String> files = fileReader.readFiles(projectPath);

        for (int i = 0; i < numWorkers; i++) {
            executorService.submit(new Worker(taskQueue, resultQueue, pattern, fileReader, lock));
        }

        for (String file : files) {
            taskQueue.put(file);
        }

        for (int i = 0; i < numWorkers; i++) {
            taskQueue.put("POISON_PILL");
        }

        for (int i = 0; i < files.size(); i++) {
            Map<String, List<String>> result = resultQueue.take();
            result.forEach((key, value) -> {
                lock.lock();
                try {
                    inheritanceIndex.computeIfAbsent(key, k -> new ArrayList<>()).addAll(value);
                } finally {
                    lock.unlock();
                }

            });
        }

        executorService.shutdown();

        return inheritanceIndex;
    }
}