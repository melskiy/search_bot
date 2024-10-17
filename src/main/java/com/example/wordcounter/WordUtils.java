package main.java.com.example.wordcounter;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WordUtils {
    public static HashMap<String, Integer> countWords(String text) {
        Pattern pattern = Pattern.compile("\\b[\\w']+\\b");
        Matcher matcher = pattern.matcher(text);

        HashMap<String, Integer> wordCount = new HashMap<>();
        while (matcher.find()) {
            String word = matcher.group().toLowerCase();
            wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
        }

        return wordCount;
    }
}