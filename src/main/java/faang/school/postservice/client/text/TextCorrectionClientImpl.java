package faang.school.postservice.client.text;

import faang.school.postservice.config.corrector.PostCorrectorProperty;
import faang.school.postservice.exception.TextAutoCorrectionException;
import faang.school.postservice.model.text.CorrectionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class TextCorrectionClientImpl {
    private final PostCorrectorProperty properties;
    private final RestTemplate restTemplate;

    @Retryable(retryFor = {RestClientResponseException .class},
            maxAttemptsExpression = "#{@retryProps.maxAttempts}",
            backoff = @Backoff(
                    delayExpression = "#{@retryProps.backoffDelay}",
                    multiplierExpression = "#{@retryProps.backoffMultiplier}"))
    public CorrectionResponse callCorrectionApi(String params) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<String> requestEntity = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<CorrectionResponse> responseEntity =
                    restTemplate.exchange(properties.apiUrl(), HttpMethod.POST, requestEntity,
                            new ParameterizedTypeReference<>() {});
            return responseEntity.getBody();
        } catch (RestClientResponseException e) {
            log.error("Failed : HTTP error while post text autocorrecting. Text & language = {}", params, e);
            throw new TextAutoCorrectionException(String.format("Failed : HTTP error code : %s", e.getStatusCode()));
        }
    }
}