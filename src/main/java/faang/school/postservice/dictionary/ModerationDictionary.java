package faang.school.postservice.dictionary;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component
public class ModerationDictionary {

    public final Set<String> censorWords = new HashSet<>();
    private final String censorWordsPath;

    public ModerationDictionary(@Value("${bad-words.path}") String censorWordsPath) {
        this.censorWordsPath = censorWordsPath;
    }

    public boolean containsCensorWord(String content) {
        var words = content.toLowerCase().trim().split(" ");
        System.out.println(Arrays.toString(words));
        return Arrays.stream(words)
                .anyMatch(censorWords::contains);
    }

    @PostConstruct
    private void loadCensorWords() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new ClassPathResource(censorWordsPath).getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                censorWords.addAll(
                        Arrays.stream(line.trim().toLowerCase().split(","))
                                .filter(word -> !word.isBlank())
                                .toList()
                );
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
