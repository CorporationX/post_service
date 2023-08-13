package faang.school.postservice.service.moderation;

import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Component
@Slf4j
public class ModerationDictionary {

    private List<String> obsceneWordsDictionary;

    public boolean checkWordContent(String content) {
        log.info(Thread.currentThread().getName() + " was started");
        String resultString = content.replaceAll("[\\W]", "")
                .replaceAll("[-_'~`]", "")
                .toLowerCase();

        return obsceneWordsDictionary.stream()
                .anyMatch(word -> resultString.contains(word));
    }

    @SneakyThrows
    @PostConstruct
    private void initDictionary() {
        Path filePath = Path.of("./src/main/resources/dictionary-of-obscene-words.txt");
        obsceneWordsDictionary = Files.readAllLines(filePath)
                .stream()
                .map(word -> word.trim().toLowerCase())
                .toList();
        log.info("Dictionary of obscene words has initialized successfully");
    }
}
