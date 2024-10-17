package main.java.com.example.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class EnvReader {
    private final Properties properties;

    public EnvReader() throws IOException {

        File envFile = new File("src/main/java/com/example/.env");

        if (envFile.exists()) {
            try (FileInputStream fis = new FileInputStream(envFile)) {
                properties = new Properties();
                properties.load(fis);
            } catch (IOException e) {
                throw new IOException("Ошибка при чтении файла .env: " + e.getMessage(), e);
            }
        } else {
            throw new IOException("Файл .env не найден.");
        }
    }

    public String getPath() {
        return properties.getProperty("PATH");
    }

    public static void main(String[] args) {
        try {
            EnvReader envReader = new EnvReader();
            String path = envReader.getPath();

            if (path != null) {
                System.out.println("Значение PATH из .env: " + path);
            } else {
                System.out.println("Переменная PATH не найдена в .env.");
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
