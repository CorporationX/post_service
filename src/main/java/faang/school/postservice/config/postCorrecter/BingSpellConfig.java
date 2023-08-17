package faang.school.postservice.config.postCorrecter;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "ai-spelling")
@Data
public class BingSpellConfig {
    private String contentType;
    private String xRapidApiKey;
    private String xRapidApiHost;

    public Map<String, Collection<String>> getHeaders() {
        Map<String, Collection<String>> headers = new HashMap<>();
        headers.put("content-type", Collections.singleton(contentType));
        headers.put("X-RapidAPI-Key", Collections.singleton(xRapidApiKey));
        headers.put("X-RapidAPI-Host", Collections.singleton(xRapidApiHost));
        return headers;
    }
}
