package faang.school.postservice.client.languagetool;

import faang.school.postservice.config.languagetool.LanguageToolConfig;
import faang.school.postservice.dto.languagetool.LanguageToolMatchDTO;
import faang.school.postservice.dto.languagetool.LanguageToolResponseDTO;
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

import java.util.Comparator;
import java.util.List;

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
    public String correctText(String text) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("text", text);
        body.add("language", languageToolConfig.getLanguage());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<LanguageToolResponseDTO> response = restTemplate.postForEntity(
                languageToolConfig.getUrl(), request, LanguageToolResponseDTO.class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new RestClientException("LanguageTool API error: " + response.getStatusCode());
        }

        return applyCorrectText(text, response.getBody());
    }

    private String applyCorrectText(String original, LanguageToolResponseDTO response) {
        StringBuilder result = new StringBuilder(original);
        List<LanguageToolMatchDTO> matches = response.matches();
        matches.sort(Comparator.comparingInt(LanguageToolMatchDTO::offset).reversed());

        for (LanguageToolMatchDTO match : matches) {
            if (!match.replacements().isEmpty()) {
                String replacement = match.replacements().get(0).value();
                result.replace(match.offset(), match.offset() + match.length(), replacement);
            }
        }
        return result.toString();
    }
}
