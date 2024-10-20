package faang.school.postservice.config.dictionary;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Component
public class OffensiveWordsFileInit {

    @Value("${dictionary.offensive.initial}")
    private String dictionaryLocation;

    @Bean
    public List<String> offensiveWords() {
        try {
            Path path = Paths.get(Objects.requireNonNull(getClass().getClassLoader()
                    .getResource(dictionaryLocation)).toURI());

            return Files.lines(path)
                    .flatMap(line -> Arrays.stream(line.toLowerCase().split("[\n\t ,.]")))
                    .toList();
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
