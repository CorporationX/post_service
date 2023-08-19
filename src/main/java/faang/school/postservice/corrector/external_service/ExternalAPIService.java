package faang.school.postservice.corrector.external_service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import faang.school.postservice.exception.NetworkException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

@Service
public class ExternalAPIService {
    private final RestTemplate restTemplate;
    private final String aiServiceUrl = "https://textgears-textgears-v1.p.rapidapi.com/grammar";
    @Value("${ai.api.key}")
    private String apiKey;

    private Language language = Language.EN_US;

    @Autowired
    public ExternalAPIService() {
        this.restTemplate = new RestTemplate();
    }

    @Retryable(value = {NetworkException.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public String correctText(String text) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("X-RapidAPI-Key", apiKey);
        headers.set("X-RapidAPI-Host", "textgears-textgears-v1.p.rapidapi.com");

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("text", text);
        requestBody.add("language", language.getCode());

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(aiServiceUrl, HttpMethod.POST, requestEntity, String.class);

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            String responseBody = responseEntity.getBody();
            return extractCorrectedTextFromJson(responseBody, text);
        } else {
            throw new NetworkException("Failed to correct text using AI service.");
        }
    }


    private String extractCorrectedTextFromJson(String jsonResponse, String originalText) {
        Gson gson = new Gson();
        try {
            JsonObject jsonObject = gson.fromJson(jsonResponse, JsonObject.class);

            if (jsonObject.has("status") && jsonObject.get("status").getAsBoolean() &&
                    jsonObject.has("response") && jsonObject.get("response").isJsonObject()) {

                JsonObject response = jsonObject.getAsJsonObject("response");
                if (response.has("result") && response.get("result").getAsBoolean() &&
                        response.has("errors") && response.get("errors").isJsonArray()) {

                    JsonArray errorsArray = response.getAsJsonArray("errors");
                    StringBuilder correctedTextBuilder = new StringBuilder(originalText);

                    for (JsonElement errorElement : errorsArray) {
                        if (errorElement.isJsonObject()) {
                            JsonObject errorObject = errorElement.getAsJsonObject();
                            if (errorObject.has("offset") && errorObject.has("better") &&
                                    errorObject.get("better").isJsonArray()) {

                                int offset = errorObject.get("offset").getAsInt();
                                JsonArray betterArray = errorObject.getAsJsonArray("better");

                                if (!betterArray.isJsonNull() && betterArray.size() > 0) {
                                    String betterSuggestion = betterArray.get(0).getAsString();
                                    if (!betterSuggestion.isEmpty()) {
                                        correctedTextBuilder.replace(offset, offset + betterSuggestion.length(), betterSuggestion);
                                    }
                                }
                            }
                        }
                    }

                    return correctedTextBuilder.toString();
                }
            }

            throw new NetworkException("Failed to extract corrected text from JSON response.");
        } catch (JsonSyntaxException e) {
            throw new NetworkException("Error parsing JSON response: " + e.getMessage());
        }
    }
}

