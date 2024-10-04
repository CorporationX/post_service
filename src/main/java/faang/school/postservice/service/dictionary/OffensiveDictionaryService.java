package faang.school.postservice.service.dictionary;

import faang.school.postservice.client.DictionaryClient;
import faang.school.postservice.config.dictionary.OffensiveWordsDictionary;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Slf4j
@Service
public class OffensiveDictionaryService {

    private final DictionaryClient dictionaryClient;
    private final ExecutorService executorService;

    public OffensiveDictionaryService(DictionaryClient dictionaryClient,
                                      @Qualifier("cachedExecutor") ExecutorService executorService) {
        this.dictionaryClient = dictionaryClient;
        this.executorService = executorService;
    }

    @Async("cachedExecutor")
    public void updateOffensiveDictionary() {
        CompletableFuture.runAsync(this::updateRussianOffensiveWords, executorService);
        CompletableFuture.runAsync(this::updateEnglishOffensiveWords, executorService);
    }

    @Retryable(retryFor = FeignException.class,
            maxAttemptsExpression = "${dictionary.offensive.update.retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${dictionary.offensive.update.retry.maxDelay}"))
    private void updateRussianOffensiveWords() {
        log.info("Trying to update Russian offensive words");
        ResponseEntity<byte[]> ruWords = dictionaryClient.getRuWords();

        if (ruWords.getBody() != null) {
            OffensiveWordsDictionary.addWordsInDictionary(convertResponseByteArrayToWords(ruWords));
        }
        log.info("Finish update Russian offensive words");
    }

    @Retryable(retryFor = FeignException.class,
            maxAttemptsExpression = "${dictionary.offensive.update.retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${dictionary.offensive.update.retry.maxDelay}"))
    private void updateEnglishOffensiveWords() {
        log.info("Trying to update English offensive words");
        ResponseEntity<byte[]> enWords = dictionaryClient.getEndWords();

        if (enWords.getBody() != null) {
            OffensiveWordsDictionary.addWordsInDictionary(convertResponseByteArrayToWords(enWords));
        }
        log.info("Finish update English offensive words");
    }

    private List<String> convertResponseByteArrayToWords(ResponseEntity<byte[]> response) {
        String words = new String(Objects.requireNonNull(response.getBody()), StandardCharsets.UTF_8);
        return Arrays.asList(words.split("[\n\t.,; ]"));
    }
}
