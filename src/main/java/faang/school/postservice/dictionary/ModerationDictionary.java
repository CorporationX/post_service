package faang.school.postservice.dictionary;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.Arrays;


@Component
@RequiredArgsConstructor
@PropertySource("classpath:moderation-dictionary.yml")
public class ModerationDictionary {
    @Value("${unwantedWords}")
    private final String[] unwantedWords;

    public boolean containsUnwantedWords(String text) {
        return Arrays.stream(unwantedWords).anyMatch(text::contains);
    }
}
