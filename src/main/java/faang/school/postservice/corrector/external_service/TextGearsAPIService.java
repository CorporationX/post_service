package faang.school.postservice.corrector.external_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.AIApiConfig;
import faang.school.postservice.dto.corrector.TextGearsApiResponse;
import faang.school.postservice.exception.NetworkException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TextGearsAPIService {
    private final RestTemplate restTemplate;
    private final AIApiConfig aiApiConfig;
    private Language language = Language.EN_US;

    @Retryable(value = {NetworkException.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public String correctText(String text) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("X-RapidAPI-Key", aiApiConfig.getKey());
        headers.set("X-RapidAPI-Host", aiApiConfig.getHost());

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("text", text);
        requestBody.add("language", language.getCode());

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);
        String url = "https://" + aiApiConfig.getHost() + aiApiConfig.getPath();
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            String responseBody = responseEntity.getBody();
            return extractCorrectedTextFromJson(responseBody, text);
        } else {
            throw new NetworkException("Failed to correct text using AI service.");
        }
    }


    private String extractCorrectedTextFromJson(String jsonResponse, String originalText) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            TextGearsApiResponse apiResponse = objectMapper.readValue(jsonResponse, TextGearsApiResponse.class);

            if (apiResponse.isStatus() &&
                    apiResponse.getResponse() != null && apiResponse.getResponse().isResult()) {

                StringBuilder correctedTextBuilder = new StringBuilder(originalText);
                List<TextGearsApiResponse.Error> errors = apiResponse.getResponse().getErrors();

                for (TextGearsApiResponse.Error error : errors) {
                    int offset = error.getOffset();
                    List<String> betterArray = error.getBetter();

                    if (betterArray != null && !betterArray.isEmpty()) {
                        String betterSuggestion = betterArray.get(0);
                        if (betterSuggestion != null && !betterSuggestion.isEmpty()) {
                            correctedTextBuilder.replace(offset, offset + betterSuggestion.length(), betterSuggestion);
                        }
                    }
                }

                return correctedTextBuilder.toString();
            }

            throw new NetworkException("Failed to extract corrected text from JSON response.");
        } catch (Exception e) {
            throw new NetworkException("Error parsing JSON response: " + e.getMessage());
        }
    }
}

