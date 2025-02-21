package faang.school.postservice.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
public class ModerationDictionaryUtil {
    private final Set<String> bannedWords;

    public ModerationDictionaryUtil(@Value("${moderation.banned-words-path}") String filePath) {
        Set<String> words = new HashSet<>();
        try {
            String content = Files.readString(Path.of(filePath));
            ObjectMapper objectMapper = new ObjectMapper();
            BannedWordsContainer container = objectMapper.readValue(content, BannedWordsContainer.class);
            words = new HashSet<>(container.bannedWords());
            log.info("Loaded {} banned words from {}", words.size(), filePath);
        } catch (IOException e) {
            log.error("Failed to load banned words from {}: {}", filePath, e.getMessage());
        }
        this.bannedWords = Collections.unmodifiableSet(words);
    }

    public boolean containsBannedWords(String content) {
        return content != null && !content.isBlank() &&
                Set.of(content.toLowerCase().split("\\W+")).stream().anyMatch(bannedWords::contains);
    }

    private record BannedWordsContainer(@JsonProperty("bannedWords") List<String> bannedWords) {}
}