package faang.school.postservice.dictionary;

import faang.school.postservice.config.moderation.ModerationConfig;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

/**
 * Компонент для загрузки и предоставления доступа к набору нецензурных слов для модерации контента.
 * <p>
 * Данный класс загружает словарь нецензурных слов из файла в classpath при инициализации
 * и предоставляет методы для доступа к загруженным словам. Путь к файлу словаря настраивается
 * через механизм инъекции свойств Spring.
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ModerationDictionary {
    private final ModerationConfig moderationConfig;

    private final Set<String> profanityWords = new HashSet<>();

    @PostConstruct
    public void init() {
        try {
            loadDictionary();
        } catch (IOException e) {
            log.error("Failed to load moderation dictionary. Shutting down.", e);
        }
    }

    private void loadDictionary() throws IOException {
        log.info("Loading moderation dictionary");
        ClassPathResource resource = new ClassPathResource(moderationConfig.getDictionaryPath());
        try (BufferedReader reader =
                     new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    profanityWords.add(line.trim().toLowerCase());
                }
            }
        }
        log.info("Loaded {} profanity word", profanityWords.size());
    }

    @Cacheable(value = "profanityDictionary", sync = true)
    public Set<String> getProfanityWords() {
        return Set.copyOf(profanityWords);
    }
}