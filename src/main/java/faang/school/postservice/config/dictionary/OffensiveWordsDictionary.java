package faang.school.postservice.config.dictionary;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
public class OffensiveWordsDictionary {

    private static final Set<String> offensiveDictionary = new CopyOnWriteArraySet<>();

    @Value("${dictionary.offensive.initial}")
    private String dictionaryLocation;

    @PostConstruct
    public void init() {
        try {
            Path path = Paths.get(Objects.requireNonNull(getClass().getClassLoader()
                    .getResource(dictionaryLocation)).toURI());

            offensiveDictionary.addAll(Files.lines(path)
                    .flatMap(line -> Arrays.stream(line.toLowerCase().split("[\n\t ,.]")))
                    .toList());
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isWordContainsInDictionary(String word) {
        return offensiveDictionary.contains(word);
    }

    public static void addWordsInDictionary(List<String> newWords) {
        offensiveDictionary.addAll(newWords);
    }
}
