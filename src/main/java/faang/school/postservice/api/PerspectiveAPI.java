package faang.school.postservice.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.exception.PerspectiveAPIException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class PerspectiveAPI {
    private static final String EMPTY_CONTENT_MESSAGE = "Content is null or empty";
    private static final String API_REQUEST_FAILED = "API request failed with status: %s";
    private static final String INVALID_RESPONSE_FORMAT = "Invalid API response format";
    private static final String API_CALL_ERROR = "Error calling Perspective API";
    private static final double TOXICITY_THRESHOLD = 0.7;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${perspective-api.key}")
    private String apiKey;

    @Value("${perspective-api.url}")
    private String apiUrl;

    public boolean isContentToxic(String text) throws PerspectiveAPIException {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException(EMPTY_CONTENT_MESSAGE);
        }

        Map<String, Object> request = Map.of(
                "comment", Map.of("text", text),
                "requestedAttributes", Map.of(
                        "TOXICITY", Map.of(),
                        "SEVERE_TOXICITY", Map.of(),
                        "INSULT", Map.of()
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        String url = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .queryParam("key", apiKey)
                .toUriString();

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                Map.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new PerspectiveAPIException(String.format(API_REQUEST_FAILED, response.getStatusCode()));
        }

        Map<String, Object> responseBody = response.getBody();
        if (responseBody == null || !responseBody.containsKey("attributeScores")) {
            throw new PerspectiveAPIException(INVALID_RESPONSE_FORMAT);
        }

        Map<String, Object> attributeScores = (Map<String, Object>) responseBody.get("attributeScores");

        return isAttributeToxic(attributeScores, "TOXICITY") ||
                isAttributeToxic(attributeScores, "SEVERE_TOXICITY") ||
                isAttributeToxic(attributeScores, "INSULT");

    }

    private boolean isAttributeToxic(Map<String, Object> attributeScores, String attribute) {
        if (!attributeScores.containsKey(attribute)) {
            return false;
        }

        Map<String, Object> attributeData = (Map<String, Object>) attributeScores.get(attribute);
        Map<String, Object> summaryScore = (Map<String, Object>) attributeData.get("summaryScore");
        Double value = (Double) summaryScore.get("value");

        return value != null && value > TOXICITY_THRESHOLD;
    }
}