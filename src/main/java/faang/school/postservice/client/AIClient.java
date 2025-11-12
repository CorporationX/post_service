package faang.school.postservice.client;


import faang.school.postservice.exception.AiServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;


@Slf4j
@Component
@RequiredArgsConstructor
public class AIClient {

    private final RestTemplate restTemplate;

    @Value("${ai.spellcheck-url:https://speller.yandex.net/services/spellservice.json/checkText}")
    private String spellcheckUrl;

    public String correctText(String text) {
        try {
            log.info("Submitting text for spell checking ({} characters)", text.length());

            String body = "text=" + URLEncoder.encode(text, StandardCharsets.UTF_8);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            HttpEntity<String> request = new HttpEntity<>(body, headers);

            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    spellcheckUrl,
                    HttpMethod.POST,
                    request,
                    new ParameterizedTypeReference<>() {
                    }
            );

            List<Map<String, Object>> corrections = response.getBody();
            if (corrections == null || corrections.isEmpty()) {
                log.info("No spelling errors found");
                return text;
            }

            String corrected = applyCorrections(text, corrections);
            log.info("Corrected text: {}", corrected);
            return corrected;

        } catch (Exception e) {
            log.error("Error accessing AI Speller: {}", e.getMessage(), e);
            throw new AiServiceException("Error connecting to the spelling service", e);
        }
    }

    private String applyCorrections(String text, List<Map<String, Object>> corrections) {
        corrections.sort((a, b) -> ((Integer) b.get("pos")) - ((Integer) a.get("pos")));
        StringBuilder sb = new StringBuilder(text);

        for (Map<String, Object> correction : corrections) {
            Object suggestionsObj = correction.get("s");
            List<String> suggestions;

            if (suggestionsObj instanceof List<?>) {
                suggestions = ((List<?>) suggestionsObj).stream()
                        .filter(item -> item instanceof String)
                        .map(item -> (String) item)
                        .toList();
            } else {
                suggestions = List.of();
            }

            if (suggestions.isEmpty()) continue;

            int pos = (int) correction.get("pos");
            int len = (int) correction.get("len");
            sb.replace(pos, pos + len, suggestions.get(0));
        }

        return sb.toString();
    }
}
