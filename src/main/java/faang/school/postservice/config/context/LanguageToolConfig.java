package faang.school.postservice.config.context;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "languagetool")
public class LanguageToolConfig {
    private String apiUrl;
    private String language;
    private int maxTextLength = 10000;
}
