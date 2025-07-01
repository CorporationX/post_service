package faang.school.postservice.config.moderation;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class ModerationDictionary {

    @Value("classpath:${dictionary.offensive.initial}")
    private Resource dictionaryFile;

    private final Set<String> offensiveWords = ConcurrentHashMap.newKeySet();

    public boolean containsOffensive(String text) {
        if (text == null || text.isBlank()) {
            return false;
        }

        String normalized = text.toLowerCase();
        BreakIterator wordIterator = BreakIterator.getWordInstance(Locale.getDefault());
        wordIterator.setText(normalized);
        List<String> tokens = new ArrayList<>();

        int start = wordIterator.first();
        for (int end = wordIterator.next(); end != BreakIterator.DONE; start = end, end = wordIterator.next()) {
            String word = normalized.substring(start, end).trim();
            if (!word.isBlank() && Character.isLetterOrDigit(word.charAt(0))) {
                tokens.add(word);
            }
        }

        return tokens.stream().anyMatch(offensiveWords::contains);
    }

    @PostConstruct
    public void load() {
        try (InputStream input = dictionaryFile.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {

            offensiveWords.clear();

            String line;
            boolean skipHeader = true;
            while ((line = reader.readLine()) != null) {
                if (skipHeader) {
                    skipHeader = false;
                    continue;
                }

                String word = line.trim().toLowerCase();
                if (!word.isBlank()) {
                    offensiveWords.add(word);
                }
            }

            log.info("Словарь загружен из CSV, {} нецензурных слов", offensiveWords.size());
        } catch (Exception e) {
            log.error("Не удалось загрузить словарь из CSV", e);
        }
    }
}
