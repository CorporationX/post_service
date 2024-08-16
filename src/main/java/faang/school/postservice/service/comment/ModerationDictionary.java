package faang.school.postservice.service.comment;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

@Component
public class ModerationDictionary {
    @Value("${comment.word-split-regex}")
    private String wordSplitRegex;
    private Set<String> badWords = new HashSet<>();

    @PostConstruct
    public void loadBadWords() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                getClass().getResourceAsStream("/bad_words_list.txt")))) {
            String word;
            while ((word = reader.readLine()) != null) {
                badWords.add(word.toLowerCase());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load forbidden words!");
        }
    }

    public boolean containsForbiddenWords(String text) {
        for (String word : text.toLowerCase().split(wordSplitRegex)) {
            if (badWords.contains(word)) {
                return true;
            }
        }
        return false;
    }
}
