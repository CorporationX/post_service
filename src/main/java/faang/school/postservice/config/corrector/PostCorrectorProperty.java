package faang.school.postservice.config.corrector;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("languagetool")
public record PostCorrectorProperty(
        String apiUrl,
        String language
) {
}