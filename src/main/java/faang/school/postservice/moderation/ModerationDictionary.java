package faang.school.postservice.moderation;

import faang.school.postservice.exception.ModerationDictionaryException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ModerationDictionary {

    private Set<String> banWords;

    @Value("${moderation.file-path}")
    private String filePath;

    public boolean containsBadWords(String content) {
        return banWords.stream()
                .anyMatch(badWord -> content.toLowerCase().contains(badWord));
    }

    @PostConstruct
    public void fillSetWithBadWords() {
        Path path = Path.of(filePath);

        try (var words = Files.lines(path)) {
            banWords = words
                    .map(String::trim)
                    .map(String::toLowerCase)
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            String errorContent = "Error during moderation dictionary filling: " + e.getMessage();
            log.error(errorContent);
            throw new ModerationDictionaryException(errorContent);
        }
    }
}
