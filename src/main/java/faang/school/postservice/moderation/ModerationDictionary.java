package faang.school.postservice.moderation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ModerationDictionary {

    private final Dictionary dictionary;

    public Map<Long, Boolean> searchSwearWords(Map<Long, String> unverifiedContent) {
        log.info("Зашли в searchSwearWords");
        Map<Long, Boolean> map = new HashMap<>();

        unverifiedContent.forEach((key, value) -> {
            boolean containsSwearWord = dictionary.getDictionary().stream()
                    .anyMatch(value::contains);

            map.put(key, !containsSwearWord);
        });
        log.info("Вышли в searchSwearWords");
        return map;
    }
}
