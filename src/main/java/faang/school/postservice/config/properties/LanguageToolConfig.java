package faang.school.postservice.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "language-tool")
@Data
public class LanguageToolConfig {
    private String url;
    private String language;
}
