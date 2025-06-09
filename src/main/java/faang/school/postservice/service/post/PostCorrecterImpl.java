package faang.school.postservice.service.post;

import com.fasterxml.jackson.databind.JsonNode;
import faang.school.postservice.config.corrector.PostCorrectorProperty;
import faang.school.postservice.exception.TextAutoCorrectionException;
import faang.school.postservice.service.PostCorrectorService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Configuration
@RequiredArgsConstructor
@Component
public class PostCorrecterImpl implements PostCorrectorService {
    private final PostCorrectorProperty properties;

    @Override
    @Retryable(retryFor = {IOException.class}, maxAttempts = 3, backoff = @Backoff(delay = 2000, multiplier = 1.5))
    public String checkText(String textToCheck) {
        StringBuilder correctedText = new StringBuilder();
        String url = properties.apiUrl();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        String params = "text=" + textToCheck + "&language=" + properties.language();
        HttpEntity<String> requestEntity = new HttpEntity<>(params, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<JsonNode> responseEntity =
                restTemplate.exchange(url, HttpMethod.POST, requestEntity,
                        new ParameterizedTypeReference<JsonNode>() {});

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            JsonNode jsonResponse = responseEntity.getBody();
            int previousEnd = 0;
            assert jsonResponse != null;
            for (JsonNode match : jsonResponse.get("matches")) {
                if (match.has("replacements") && !match.get("replacements").isEmpty()) {
                    String replacement = match.get("replacements").get(0).get("value").asText();
//                        Извлекается первое возможное исправление для найденной ошибки.
                    int offset = match.get("offset").asInt();
                    int length = match.get("length").asInt();
                    correctedText.append(textToCheck, previousEnd, offset);
                    correctedText.append(replacement);
                    previousEnd = offset + length;
                }
            }
            correctedText.append(textToCheck.substring(previousEnd));
        } else {
            throw new TextAutoCorrectionException("Failed : HTTP error code : " + responseEntity.getStatusCode());
        }
        return correctedText.toString();
    }
}
