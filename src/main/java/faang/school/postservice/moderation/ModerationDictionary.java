package faang.school.postservice.moderation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ModerationDictionary {

    private final Dictionary dictionary;

    public Map<Long, Boolean> searchSwearWords(Map<Long, String> unverifiedContent) {
        Map<Long, Boolean> verifiedContent = new HashMap<>();

        unverifiedContent.forEach((key, value) -> {
            boolean containsSwearWord = dictionary.getDictionary().stream()
                    .anyMatch(value::contains);

            verifiedContent.put(key, !containsSwearWord);
        });

        return verifiedContent;
    }
}
