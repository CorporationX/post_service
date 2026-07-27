package faang.school.postservice.component;

import faang.school.postservice.repository.PostRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class ModerationDictionary {
    private final PostRepository postRepository;


    @Value("${moderation.dictionary.file}")
    private String dictionaryFilePath;
    private Set<String> profaneWords;

    @PostConstruct
    private void loadDictionary() {
        profaneWords = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream(dictionaryFilePath)), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                profaneWords.add(line.trim().toLowerCase());
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public boolean containsProfanity(String content) {
        String[] words = content
                .replaceAll("[^a-zA-Zа-яА-Я0-9]", " ")
                .replaceAll("\\s+", " ")
                .split(" ");
        if (words.length == 0) {
            words = new String[]{content.toLowerCase()};
        }
        for (String word : words) {
            if (profaneWords.contains(word)) {
                return true;
            }
        }
        return false;
    }

}
