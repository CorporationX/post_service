package faang.school.postservice.config;

import faang.school.postservice.moderator.ModerationDictionary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Set;

@Configuration
public class ModerationDictionaryConfig {
    @Value("classpath:insultsDictionary.txt")
    private String insultsDictionary;

    @Bean
    public ModerationDictionary moderationDictionary() {
        Set<String> words=loadDictionary(insultsDictionary);
        return new ModerationDictionary(words);
    }

    private  Set<String> loadDictionary(String filePath) {
        Set<String> words = new HashSet<>();
        try(BufferedReader reader = new BufferedReader(new FileReader(ResourceUtils.getFile(filePath)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // метод для удаления случайных пробелов в начале и конце строки
                words.add(line.trim());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return words;
    }
}
