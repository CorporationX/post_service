package faang.school.postservice.service.moderation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Service
public class ModerationDictionary {
    private final Set<String> badWords;

    public ModerationDictionary(@Value("${moderation.badWords.list}") String[] badWords) {
        this.badWords = new HashSet<>(Arrays.asList(badWords));
    }

    public boolean containsBadWords(String text) {
        String[] words = text.toLowerCase().split("\\s+");
        return Arrays.stream(words)
                .anyMatch(badWords::contains);
    }
}
