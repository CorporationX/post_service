package faang.school.postservice.validation;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class ModerationDictionary {

    private static final Logger log = LoggerFactory.getLogger(ModerationDictionary.class);
    private final Set<String> badWords;

    public ModerationDictionary() {
        this.badWords = loadBadWords();
    }

    private Set<String> loadBadWords() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new ClassPathResource("bad-words.txt").getInputStream(), StandardCharsets.UTF_8))) {
            return reader.lines().map(String::toLowerCase).collect(Collectors.toSet());
        } catch (Exception e) {
            log.error("Error loading bad words list. AI moderation will still work.", e);
            return new HashSet<>();
        }
    }

    public boolean containsBadWord(String text) {
        if (text == null || text.isBlank()) {
            log.warn("Bad words list contains null or empty string.");
            return false;
        }
        String lowerText = text.toLowerCase();
        return badWords.stream().anyMatch(lowerText::contains);
    }
}
