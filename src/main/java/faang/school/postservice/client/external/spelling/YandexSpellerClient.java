package faang.school.postservice.client.external.spelling;

import faang.school.postservice.dto.spelling_corrector.yandex_speller.YandexSpellerCorrectResponse;
import faang.school.postservice.exception.spelling_corrector.DontRepeatableServiceException;
import faang.school.postservice.exception.spelling_corrector.RepeatableServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Slf4j
@Component
@RequiredArgsConstructor
public class YandexSpellerClient {
    private final RestTemplate checkSpellerClient;

    @Value("${post.spelling-corrector.client.yandex.url}")
    private String serviceUrl;

    @Retryable(retryFor = {RepeatableServiceException.class}, backoff = @Backoff(
            delayExpression = "#{${post.spelling-corrector.retry.delay}}",
            multiplierExpression = "#{${post.spelling-corrector.retry.multiplier}}"))
    public String correctText(String text) {
        try {
            ResponseEntity<YandexSpellerCorrectResponse[]> responseEntity = checkSpellerClient
                    .getForEntity(makeUri(text), YandexSpellerCorrectResponse[].class);

            YandexSpellerCorrectResponse[] response = responseEntity.getBody();

            if (response != null) {
                return prepareText(response, text);
            }

            return text;
        } catch (HttpClientErrorException exception) {
            int statusCode = exception.getStatusCode().value();

            log.error("Ошибка {} при получении корректировки от YandexSpeller", statusCode, exception);

            if (statusCode >= BAD_REQUEST.value() && statusCode < INTERNAL_SERVER_ERROR.value()) {
                throw new DontRepeatableServiceException();
            }

            if (statusCode >= INTERNAL_SERVER_ERROR.value()) {
                throw new RepeatableServiceException();
            }
        }

        return text;
    }

    private String prepareText(YandexSpellerCorrectResponse[] correctWords, String text) {
        StringBuilder textPost = new StringBuilder(text);
        StringBuilder correctText = new StringBuilder();
        int lastPos = 0;

        for (YandexSpellerCorrectResponse correctWord : correctWords) {
            int pos = correctWord.getPos();
            String word = correctWord.getS().get(0);
            correctText.append(textPost.substring(lastPos, pos));
            correctText.append(word);
            lastPos = pos + correctWord.getLen();
        }

        correctText.append(textPost.substring(lastPos));

        return correctText.toString();
    }

    private URI makeUri(String text) {
        return UriComponentsBuilder.fromHttpUrl(serviceUrl)
                .queryParam("text", text)
                .build()
                .encode()
                .toUri();
    }
}