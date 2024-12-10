package faang.school.postservice.service.grammar;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class GrammarBot {


    @Value("${grammarbot.url}")
    private String apiUrl;

    @Value("${grammarbot.api-key}")
    private String apiKey;

    @Value("${grammarbot.host}")
    private String apiHost;

    public String checkGrammar(String text) {
        try {
            String requestBody = "text=" + text.replace(" ", "%20") + "&language=en-US";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("x-rapidapi-key", apiKey)
                    .header("x-rapidapi-host", apiHost)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            return response.body();
        } catch (Exception e) {
            throw new RuntimeException("Failed to call GrammarBot API", e);
        }
    }

}
