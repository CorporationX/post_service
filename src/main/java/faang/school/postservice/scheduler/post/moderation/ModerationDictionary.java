package faang.school.postservice.scheduler.post.moderation;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ModerationDictionary {
    private final Set<String> badWords = new HashSet<>();

    public ModerationDictionary() {
        loadBadWords("moderation/badwords_en.txt");
        loadBadWords("moderation/badwords_ru.txt");
    }

    private void loadBadWords(String filePath) {
        try {
            Path path = new ClassPathResource(filePath).getFile().toPath();
            List<String> lines = Files.readAllLines(path);
            badWords.addAll(lines.stream()
                    .map(String::trim)
                    .map(String::toLowerCase)
                    .collect(Collectors.toSet()));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load bad words from " + filePath, e);
        }
    }

    public Set<String> getBadWords() {
        return badWords;
    }

    public boolean containsBadWord(String word) {
        return badWords.contains(word.toLowerCase());
    }
}