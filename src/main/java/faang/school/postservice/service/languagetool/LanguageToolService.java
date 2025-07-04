package faang.school.postservice.service.languagetool;

import faang.school.postservice.dto.languagetool.LanguageToolResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@RequiredArgsConstructor
public class LanguageToolService {
    @Qualifier(value = "restTemplateForLanguageTool")
    private final RestTemplate restTemplate;

    @Value("${app.correction.url}")
    private String url;

    @Value("${app.correction.language}")
    private String language;

    @Retryable(
            retryFor = {RestClientResponseException.class, RestClientException.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public LanguageToolResponse checkText(String originalText) {
        MultiValueMap<String, String> formData = null;
        try {
            formData = new LinkedMultiValueMap<>();
            formData.add("text", originalText);
            formData.add("language", language);

            log.debug("Sending request to LanguageTool for text: {}", originalText);
            LanguageToolResponse response = restTemplate.postForObject(url, formData, LanguageToolResponse.class);
            return response;
        } catch (RestClientException e) {
            log.error("External API request failed", e);
            throw new RestClientException("Failed to check text with LanguageTool");
        }
    }
}
