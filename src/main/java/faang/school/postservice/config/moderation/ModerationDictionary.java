package faang.school.postservice.config.moderation;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
public class ModerationDictionary {

    @Value("classpath:${dictionary.offensive.initial}")
    private Resource dictionaryFile;

    private final Set<String> offensiveWords = new HashSet<>();

    public boolean containsOffensive(String text) {
        if (text == null || text.isBlank()) {
            return false;
        }

        String normalized = text.toLowerCase();
        return offensiveWords.stream().anyMatch(normalized::contains);
    }

    @PostConstruct
    public void load() {
        try (InputStream input = dictionaryFile.getInputStream()) {
            Yaml yaml = new Yaml();
            ModerationWordsHolder data = yaml.loadAs(input, ModerationWordsHolder.class);
            if (data.getOffensiveWords() != null) {
                offensiveWords.addAll(data.getOffensiveWords().stream()
                        .map(String::toLowerCase)
                        .map(String::trim)
                        .toList());
            }
            log.info("Словарь загружен, {} нецензурных слов", offensiveWords.size());
        } catch (Exception e) {
            log.error("Не удалось загрузить словарь нецензурных слов", e);
        }
    }

    @Setter
    @Getter
    public static class ModerationWordsHolder {
        private List<String> offensiveWords;
    }
}
