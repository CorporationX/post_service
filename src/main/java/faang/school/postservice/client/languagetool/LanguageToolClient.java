package faang.school.postservice.client.languagetool;

import faang.school.postservice.config.properties.LanguageToolConfig;
import faang.school.postservice.dto.languagetool.LanguageToolResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class LanguageToolClient {

    private final RestTemplate restTemplate;
    private final LanguageToolConfig languageToolConfig;

    @Retryable(
            retryFor = {RestClientException.class},
            maxAttemptsExpression = "${post.correcter.retry.max-attempts}",
            backoff = @Backoff(delayExpression = "${post.correcter.retry.delay}")
    )
    public LanguageToolResponseDto correctText(String text) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("text", text);
        body.add("language", languageToolConfig.getLanguage());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<LanguageToolResponseDto> response = restTemplate.postForEntity(
                languageToolConfig.getUrl(), request, LanguageToolResponseDto.class);

        return response.getBody();
    }
}
