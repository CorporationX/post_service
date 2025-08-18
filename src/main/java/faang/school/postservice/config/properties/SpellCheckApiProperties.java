package faang.school.postservice.config.properties;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "textgears")
public record SpellCheckApiProperties(
        @NotBlank String apiKey,
        @NotBlank String host,
        @NotBlank String url
) {
}
