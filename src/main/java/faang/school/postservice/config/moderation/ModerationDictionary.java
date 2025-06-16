package faang.school.postservice.config.moderation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class ModerationDictionary {

    private final Set<String> profanityWords = new HashSet<>();
    private final ObjectMapper objectMapper;

    @Value("classpath:dictionary/profanity-dictionary.json")
    private Resource dictionaryResource;


    @PostConstruct
    public void init() {
        try {
            JsonNode root = objectMapper.readTree(dictionaryResource.getInputStream());
            JsonNode wordsNode = root.get("profanity-words");
            
            if (Objects.nonNull(wordsNode) && wordsNode.isArray()) {
                wordsNode.forEach(word -> profanityWords.add(word.asText().toLowerCase()));
            }

            log.info("Loaded words: {}", profanityWords);

        } catch (IOException e) {
            log.error("Failed to load moderation dictionary");
        }
    }

    public boolean containsProfanity(String text) {
        if (Objects.isNull(text) || text.isEmpty()) {
            return false;
        }

        String lowerText = text.toLowerCase();
        return profanityWords.stream()
                .anyMatch(lowerText::contains);
    }

} 