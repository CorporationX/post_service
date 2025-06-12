package faang.school.postservice.client.language_tool;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "services.language-tool")
@Data
@Component
public class LanguageToolConfigurationProperties {
    private String url;
    private String version;
}
