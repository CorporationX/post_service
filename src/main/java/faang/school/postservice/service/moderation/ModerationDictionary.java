package faang.school.postservice.service.moderation;

import faang.school.postservice.config.ModerationProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
@Validated
public class ModerationDictionary {

    private final ModerationProperties moderationProperties;
    private final Set<String> badWords = new HashSet<>();

    private Resource badWordsResource;

    @PostConstruct
    public void init() {
        badWordsResource = moderationProperties.getDictionary();
        try {
            loadBadWords();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load bad words dictionary", e);
        }
    }

    private void loadBadWords() throws IOException {
        if (!badWordsResource.exists()) {
            throw new FileNotFoundException(
                    "Bad words dictionary not found at: " + badWordsResource.getURI());
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(badWordsResource.getInputStream()))) {
            String word;
            while ((word = reader.readLine()) != null) {
                if (!word.isBlank()) {
                    badWords.add(word.trim().toLowerCase());
                }
            }
        }
    }

    public boolean containsBadWord(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }

        String lowerText = text.toLowerCase();
        Optional<String> foundBadWord = badWords.stream()
                .filter(lowerText::contains)
                .findFirst();

        foundBadWord.ifPresent(word ->
                log.debug("Found bad word '{}' in text: {}", word, text));

        return foundBadWord.isPresent();
    }
}
