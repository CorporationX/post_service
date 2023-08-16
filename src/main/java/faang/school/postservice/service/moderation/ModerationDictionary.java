package faang.school.postservice.service.moderation;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Slf4j
public class ModerationDictionary {

    private Set<String> obsceneWordsDictionary;

    public boolean checkWordContent(String content) {
        log.info(Thread.currentThread().getName() + " was started");
        String[] resultStrings = content.replaceAll("[^a-zA-ZА-Яа-я0-9\s]", "")
                .toLowerCase()
                .split(" ");

        return Stream.of(resultStrings)
                .anyMatch(word -> obsceneWordsDictionary.contains(word));
    }

    @PostConstruct
    private void initDictionary() {
        Path filePath = Path.of("./src/main/resources/dictionary-of-obscene-words.txt");
        try {
            obsceneWordsDictionary = Files.readAllLines(filePath)
                    .stream()
                    .map(word -> word.trim().toLowerCase())
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            log.error("IOException has occurred while file:" +
                    " 'dictionary-of-obscene-words.txt' was reading");
            throw new RuntimeException(e);
        }
        log.info("Dictionary of obscene words has initialized successfully");
    }
}
