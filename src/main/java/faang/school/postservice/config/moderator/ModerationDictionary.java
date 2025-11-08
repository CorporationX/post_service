package faang.school.postservice.config.moderator;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Getter
@ConfigurationProperties
@Component
public class ModerationDictionary {

    private static final String KEY_FOR_BAN_WORDS = "ban_words";

    @Value("${ban.words.default}")
    private static List<String> defaultBanWords;

    private List<String> banWords;

    @PostConstruct
    public void loadBanWords() {
        ObjectMapper objectMapper = new ObjectMapper();
        ClassPathResource resource = new ClassPathResource("ban-word.json");
        try {
            Map<String, List<String>> data = objectMapper.readValue(resource.getInputStream(),
                    new TypeReference<>() {
                    });

            List<String> words = data.get(KEY_FOR_BAN_WORDS);
            this.banWords = doToLowerCase(words);
        } catch (Exception e) {
            log.error("Reading from file ban_word.json failed with an error.", e);
            this.banWords = defaultBanWords;
        }
    }

    public boolean containsBanWord(String text) {
        if (text == null) {
            return true;
        }

        String lowerText = text.toLowerCase();
        return banWords.stream()
                .noneMatch(lowerText::contains);
    }

    private List<String> doToLowerCase(List<String> list) {
        return list.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());
    }
}
