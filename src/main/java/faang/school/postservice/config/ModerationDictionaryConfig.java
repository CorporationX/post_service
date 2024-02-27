package faang.school.postservice.config;

import faang.school.postservice.moderator.CommentModerationDictionary;
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
    @Value("classpath:dictionary/commentModerationDictionary.txt")
    private String insultsDictionary;

    @Bean
    public CommentModerationDictionary moderationDictionary() {
        Set<String> words = loadDictionary(insultsDictionary);
        return new CommentModerationDictionary(words);
    }

    private Set<String> loadDictionary(String filePath) {
        Set<String> words = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(ResourceUtils.getFile(filePath)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                words.add(line.trim());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return words;
    }
}
