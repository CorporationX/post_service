package faang.school.postservice.config.postCorrecter;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "ai-spelling")
@Data
public class BingSpellConfig {
    private String contentType;
    private String xRapidApiKey;
    private String xRapidApiHost;
    private String mode;

    public Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("content-type", contentType);
        headers.put("X-RapidAPI-Key", xRapidApiKey);
        headers.put("X-RapidAPI-Host", xRapidApiHost);
        return headers;
    }
}
