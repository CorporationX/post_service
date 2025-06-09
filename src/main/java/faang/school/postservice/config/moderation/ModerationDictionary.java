package faang.school.postservice.config.moderation;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class ModerationDictionary {

    @Value("classpath:${dictionary.offensive.initial}")
    private Resource dictionaryFile;

    private final Set<String> offensiveWords = ConcurrentHashMap.newKeySet();;

    public boolean containsOffensive(String text) {
        if (text == null || text.isBlank()) {
            return false;
        }

        String normalized = text.toLowerCase();
        List<String> tokens = List.of(normalized.split("\\W+"));
        return tokens.stream().anyMatch(offensiveWords::contains);
    }

    @PostConstruct
    public void load() {
        try (InputStream input = dictionaryFile.getInputStream()) {
            List<String> words = parseYamlToList(input);
            offensiveWords.addAll(words.stream()
                    .map(String::toLowerCase)
                    .map(String::trim)
                    .toList());

            log.info("Словарь загружен, {} нецензурных слов", offensiveWords.size());
        } catch (Exception e) {
            log.error("Не удалось загрузить словарь нецензурных слов", e);
        }
    }

    private List<String> parseYamlToList(InputStream input) {
        Yaml yaml = new Yaml();
        ModerationWordsHolder data = yaml.loadAs(input, ModerationWordsHolder.class);
        return data != null && data.getOffensiveWords() != null ? data.getOffensiveWords() : List.of();
    }
}
