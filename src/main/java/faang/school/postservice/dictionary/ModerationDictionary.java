package faang.school.postservice.dictionary;

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

    public ModerationDictionary() {
        loadCensorWords();
    }

    public boolean containsCensorWord(String content) {
        var words = content.toLowerCase().trim().split(" ");
        System.out.println(Arrays.toString(words));
        return Arrays.stream(words)
                .anyMatch(censorWords::contains);
    }


    private void loadCensorWords() {
        Set<String> words = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new ClassPathResource("files/bad-words.csv").getInputStream()))) {
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
