package faang.school.postservice.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Component
@RequiredArgsConstructor
public class Spelling {
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;
    @Value("${ai_spelling.url}")
    private String urlPattern;

    @Async("executorService")
    @Retryable(retryFor = {RestClientException.class}, maxAttempts = 5, backoff = @Backoff(delay = 1000, multiplier = 3))
    public CompletableFuture<Optional<String>> check(String content) {
        String url = urlPattern + content;
        List<SpellingMap> spellings;

        byte[] spellResult = restTemplate.getForObject(url, byte[].class);
        try {
            spellings = objectMapper.readValue(spellResult, new TypeReference<List<SpellingMap>>() {
            });
        } catch (IOException e) {
            log.error("Error convert json to pojo-object SpellingMap", e);
            throw new RuntimeException("Error convert json ty pojo-object SpellingMap");
        }

        if (spellings.isEmpty()) {
            return CompletableFuture.completedFuture(Optional.empty());
        }
        return CompletableFuture.completedFuture(Optional.of(apply(content, spellings)));
    }

    private String apply(String content, List<SpellingMap> spellings) {
        AtomicReference<String> newContent = new AtomicReference<>(content);

        spellings.forEach(spelling ->
                newContent.set(newContent.get().replace(spelling.word, spelling.getSpellings()[0]))
        );
        return newContent.get();
    }
}