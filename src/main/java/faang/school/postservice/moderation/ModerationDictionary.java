package faang.school.postservice.moderation;

import faang.school.postservice.exception.ModerationDictionaryException;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@Getter
public class ModerationDictionary {

    private Set<String> banWords;

    @Value("${moderation.file-path}")
    private String filePath;

    @PostConstruct
    public void fillSetWithBadWords() {
        Path path = Paths.get(filePath);

        try (var words = Files.lines(path)) {
            banWords = words
                    .map(String::trim)
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            throw new ModerationDictionaryException("Error during moderation dictionary filling: " + e.getMessage());
        }
    }
}
