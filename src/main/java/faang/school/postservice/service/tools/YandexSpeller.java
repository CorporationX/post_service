package faang.school.postservice.service.tools;


import faang.school.postservice.dto.post.SpellCheckerDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Slf4j
public class YandexSpeller {
    private final String url;
    private final RestTemplate restTemplate;

    @Retryable(
            retryFor = RestClientException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000),
            recover = "recoverCheckText"
    )
    public List<SpellCheckerDto> checkText(String text) {
        String requestUrl = addParamToUrl("text", text, url);
        SpellCheckerDto[] response = restTemplate.getForObject(requestUrl, SpellCheckerDto[].class);
        return Arrays.asList(Objects.requireNonNull(response));
    }

    @Recover
    public List<SpellCheckerDto> recoverCheckText(RestClientException exception, String text) {
        log.error("Bad connection. {}", exception.getMessage());
        return Collections.emptyList();
    }

    public String correctText(String text, List<SpellCheckerDto> checkers) {
        StringBuilder correctedText = new StringBuilder(text);

        Collections.reverse(checkers);
        checkers.forEach(spellChecker -> {
            int position = spellChecker.getPos();
            int length = spellChecker.getLen();
            String correctedWord = spellChecker.getS().get(0);

            correctedText.replace(position, position + length, correctedWord);
        });

        return correctedText.toString();
    }

    private String addParamToUrl(String param, String value, String url) {
        if (!url.contains("?")) {
            url += "?";
        } else {
            url += "&";
        }
        return url + param + "=" + value;
    }
}
