package faang.school.postservice.util.moderation;

import faang.school.postservice.exception.DictionarySourceException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;

import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class ModerationDictionary {

    private Set<String> swearWords;

    @Value("${post.moderation.file-path}")
    private String filePath;

    @PostConstruct
    public void init() {
        try {
            swearWords = new HashSet<>(Files.readAllLines(Paths.get(filePath)));
            log.info("Swear words successfully loaded from file: {}", filePath);
        } catch (IOException e) {
            log.error("Failed to load swear words from file: {}", filePath, e);
            throw new DictionarySourceException("Failed to load swear words from file: " + filePath);
        }
    }

    public boolean containsSwearWords(String content) {
        return swearWords.stream()
                .anyMatch(swearWord -> content.toLowerCase().contains(swearWord));
    }
}
