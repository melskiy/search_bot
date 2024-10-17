package main.java.com.example.core;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

public interface SearchService<T> {
    Map<String, List<T>> buildInheritanceIndex(String projectPath,  Pattern pattern) throws IOException, InterruptedException, ExecutionException;
}
