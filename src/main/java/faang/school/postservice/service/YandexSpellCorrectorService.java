package faang.school.postservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.client.web.YandexSpellerClient;
import faang.school.postservice.dto.spelling.WordDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.text.BreakIterator;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class YandexSpellCorrectorService {
    private final YandexSpellerClient client;

    /**
     * Processes the input text by checking and correcting its words using a spell-checking service,
     * based on the provided WordDto[] data. Words are replaced with suggested corrections if available.
     *
     * @param text The input text to be corrected.
     * @return The corrected text with words replaced by suggested corrections, if available.
     */
    @Retryable(
            exceptionExpression = "#{message.contains('Error processing text')}",
            maxAttemptsExpression = "${yandex-speller.max-attempts-retry}",
            backoff = @Backoff(delayExpression = "${yandex-speller.delay}")
    )
    public String getCorrectedText(String text) {
        String spellText = client.checkText(text);
        ObjectMapper mapper = new ObjectMapper();
        WordDto[] words;

        try {
            words = mapper.readValue(spellText, WordDto[].class);

            Map<String, String> replacementMap = Arrays.stream(words)
                    .collect(Collectors.toMap(
                            WordDto::getWord,
                            wordInfo -> wordInfo.getS().get(0),
                            (existingValue, newValue) -> existingValue));

            StringBuilder correctedText = new StringBuilder(text.length());

            BreakIterator wordIterator = BreakIterator.getWordInstance(YandexSpellerClient.RUSSIAN_LOCALE);
            wordIterator.setText(text);

            int start = wordIterator.first();
            for (int end = wordIterator.next(); end != BreakIterator.DONE; start = end, end = wordIterator.next()) {
                String word = text.substring(start, end);
                String correctedWord = replacementMap.getOrDefault(word, word);
                correctedText.append(correctedWord);
                correctedText.append(text, start + word.length(), end);
            }

            return correctedText.toString();
        } catch (Exception e) {
            log.error("Error processing text: {}", e.getMessage());
        }

        return text;
    }
}
