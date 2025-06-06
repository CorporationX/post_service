package faang.school.postservice.config.corrector;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.exception.TextAutoCorrectionException;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Configuration
@ConfigurationPropertiesScan
@RequiredArgsConstructor
@Component
public class PostCorrecter {
    private final PostCorrectorProperty properties;

    @Retryable(value = {IOException.class}, maxAttempts = 3, backoff = @Backoff(delay = 2000, multiplier = 1.5))
    public String checkText(String textToCheck) {
        StringBuilder correctedText = new StringBuilder();
        try {
            String url = properties.apiUrl();
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setDoOutput(true);
            String params = "text=" + textToCheck + "&language=" + properties.language();

            try (OutputStream os = connection.getOutputStream()) {
                os.write(params.getBytes());
                os.flush();
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = input.readLine()) != null) {
                    response.append(inputLine);
                }
                input.close();

                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonResponse = objectMapper.readTree(response.toString());
                int previousEnd = 0;
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
                throw new TextAutoCorrectionException("Failed : HTTP error code : " + responseCode);
            }
        } catch (IOException e) {
            throw new TextAutoCorrectionException("Something went wrong. Text could not be processed for autocorrection.");
        }
        return correctedText.toString();
    }
}
