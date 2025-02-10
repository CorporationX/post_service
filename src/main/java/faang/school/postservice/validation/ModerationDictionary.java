package faang.school.postservice.validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;

@Component
public class ModerationDictionary {

    private static final Logger log = LoggerFactory.getLogger(ModerationDictionary.class);
    private final Set<String> badWords;

    public ModerationDictionary(@Value("classpath:moderation/bad-words.txt") Resource resource) {
        try {
            this.badWords = new HashSet<>(Files.readAllLines(resource.getFile().toPath()));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read txt file",e);
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
