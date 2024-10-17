package main.java.com.example.wordcounter;


import java.util.Map;

public class WordCounter {
    public static void main(String[] args) {
        FileReader reader = new FileReader();
        String text = reader.readFile("src/main/java/com/example/wordcounter/text/lorem_ipsum.txt");

        Map<String, Integer> wordCount = WordUtils.countWords(text);

        for (Map.Entry<String, Integer> entry : wordCount.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}