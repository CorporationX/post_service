package faang.school.postservice.moderation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ModerationDictionaryComment {

    private final Set<String> badWords;

    public ModerationDictionaryComment(@Value("classpath:moderation-dictionary.txt") Resource resource) throws IOException {
        this.badWords = new HashSet<>(
                Files.readAllLines(resource.getFile().toPath())
                        .stream()
                        .map(String::toLowerCase)
                        .collect(Collectors.toSet())
        );
        log.info("Loaded {} bad words from moderation dictionary", badWords.size());
    }

    public boolean containsBadWords(String text) {
        if (text == null || text.isBlank()) return false;
        String lower = text.toLowerCase();
        return badWords.stream().anyMatch(lower::contains);
    }
}