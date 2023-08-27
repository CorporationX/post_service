package faang.school.postservice.moderation;

import faang.school.postservice.exception.ModerationDictionaryException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

@Component
public class ModerationDictionary {
    private Set<String> bannedWords;

    public ModerationDictionary() {
        this.bannedWords = loadBannedWords();
    }

    public boolean containsBannedWord(String text) {
        return bannedWords.stream()
                .anyMatch(text::contains);
    }

    private Set<String> loadBannedWords() {
        Set<String> words = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new ClassPathResource("moderation_dictionary.txt").getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                words.add(line.trim());
            }
        } catch (IOException e) {
            throw new ModerationDictionaryException("Error loading banned words", e);
        }
        return words;
    }
}
