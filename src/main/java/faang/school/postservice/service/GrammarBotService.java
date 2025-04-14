package faang.school.postservice.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GrammarBotService {

    private final RestTemplate restTemplate;

    @Value("${grammar-bot.api.url}")
    private String apiUrl;

    @Value("${grammar-bot.api.key}")
    private String apiKey;

    public GrammarBotService() {
        this.restTemplate = new RestTemplate();
    }

    @Async("fileUploadTaskExecutor")
    public String checkGrammar(String text) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-rapidapi-key", apiKey);
        headers.set("x-rapidapi-host", "grammarbot.p.rapidapi.com");
        headers.set("Content-Type", "application/x-www-form-urlencoded");

        String body = "text=" + text + "&language=en-US";

        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            JSONObject jsonResponse = new JSONObject(response.getBody());

            JSONArray matches = jsonResponse.getJSONArray("matches");

            StringBuilder correctedText = new StringBuilder(text);
            for (int i = 0; i < matches.length(); i++) {
                JSONObject match = matches.getJSONObject(i);

                String replacement = match.getJSONArray("replacements").getJSONObject(0).getString("value");
                int offset = match.getInt("offset");
                int length = match.getInt("length");

                correctedText.replace(offset, offset + length, replacement);
            }

            return correctedText.toString();
        } else {
            throw new RuntimeException("Error connecting to GrammarBot API");
        }
    }
}
