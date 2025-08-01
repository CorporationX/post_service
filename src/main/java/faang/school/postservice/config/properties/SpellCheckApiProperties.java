package faang.school.postservice.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "textgears")
public record SpellCheckApiProperties(
        String apiKey,
        String host,
        String url
) {
}
