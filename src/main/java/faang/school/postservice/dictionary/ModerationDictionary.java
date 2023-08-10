package faang.school.postservice.dictionary;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;


@Component
@RequiredArgsConstructor
public class ModerationDictionary {
    private List<String> badWords;

    @PostConstruct
    private void initialize(){
        Path path = Path.of("./src/main/resources/dictionary.txt");
        try {
            badWords = Files.readAllLines(path).stream().map(word -> word.trim().toLowerCase()).toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public boolean containsUnwantedWords(String text) {
        return badWords.stream().anyMatch(text.toLowerCase()::contains);
    }
}
