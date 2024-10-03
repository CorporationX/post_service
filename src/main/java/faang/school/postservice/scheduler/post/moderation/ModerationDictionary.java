package faang.school.postservice.scheduler.post.moderation;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

@Component
public class ModerationDictionary {
    private final Set<String> badWords = new HashSet<>();

    @PostConstruct
    public void init() {
        try (InputStream inputStream = getClass().getResourceAsStream("/moderation/badwords.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                badWords.add(line.trim().toLowerCase());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load bad words from configuration file", e);
        }
    }

    public Set<String> getBadWords() {
        return badWords;
    }

    public boolean containsBadWord(String word) {
        return badWords.contains(word.toLowerCase());
    }
}
