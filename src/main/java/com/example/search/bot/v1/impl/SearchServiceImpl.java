package main.java.com.example.search.bot.v1.impl;

import main.java.com.example.core.ClassInfo;
import main.java.com.example.core.FileReader;
import main.java.com.example.core.SearchService;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SearchServiceImpl implements SearchService{
    private final FileReader fileReader;

    public SearchServiceImpl(FileReader fileReader) {
        this.fileReader = fileReader;
    }

    public Map<String, List<String>> buildInheritanceIndex(String projectPath, Pattern pattern ) throws IOException {
        List<String> files = fileReader.readFiles(projectPath);

        return files.stream()
                .flatMap(file -> {
                    try {
                        String content = fileReader.readFileContent(file);
                        Matcher matcher = pattern.matcher(content);
                        return matcher.results()
                                .map(result -> new ClassInfo(result.group(2), result.group(4)))
                                .filter(info -> info.parentName() != null)
                                .toList().stream();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.groupingBy(ClassInfo::parentName,
                        Collectors.mapping(ClassInfo::className, Collectors.toList())));
    }

}
