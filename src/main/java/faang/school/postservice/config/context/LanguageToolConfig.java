package faang.school.postservice.config.context;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
//for PR
@Data
@Component
@ConfigurationProperties(prefix = "languagetool")
public class LanguageToolConfig {
    private String apiUrl;
    private String language;
}
