package faang.school.postservice.validation;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class ModerationDictionary {

    private static final Logger log = LoggerFactory.getLogger(ModerationDictionary.class);
    private final Set<String> badWords;

    public ModerationDictionary(@Value("${moderation.bad-words-path}") Resource resource) {
        try {
            try (InputStream is = resource.getInputStream()) {
                ObjectMapper mapper = new ObjectMapper();
                List<String> words = mapper.readValue(is, new TypeReference<>() {});
                this.badWords = new HashSet<>(words);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read JSON file", e);
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
