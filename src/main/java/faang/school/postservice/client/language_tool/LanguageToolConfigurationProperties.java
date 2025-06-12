package faang.school.postservice.client.language_tool;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "services.language-tool")
@Data
public class LanguageToolConfigurationProperties {
    private String url;
    private String version;
}
