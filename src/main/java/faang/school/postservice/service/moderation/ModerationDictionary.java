package faang.school.postservice.service.moderation;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import faang.school.postservice.model.Comment;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Setter
@Slf4j
public class ModerationDictionary {

    private final Set<String> profanityWords = new HashSet<>();
    private Set<String> obsceneWordsDictionary;
    @Value("classpath:profanity-words.txt")
    private Resource profanityWordsFile;

    public boolean checkWordContent(String content) {
        log.info(Thread.currentThread().getName() + " was started");
        String[] resultStrings = content.replaceAll("[^a-zA-ZА-Яа-я0-9\s]", "")
                .toLowerCase()
                .split(" ");

        return Stream.of(resultStrings)
                .anyMatch(word -> obsceneWordsDictionary.contains(word));
    }

    public void checkComment(Comment comment) {
        String[] words = comment.getContent().toLowerCase().split("\\s+");
        comment.setVerifiedDate(LocalDateTime.now());
        for (String word : words) {
            if (profanityWords.contains(word)) {
                comment.setVerified(false);
                return;
            }
        }
        comment.setVerified(true);
    }


    @PostConstruct
    public void initProfanityWords() throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(profanityWordsFile.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                profanityWords.add(line.trim().toLowerCase());
            }
        }
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
