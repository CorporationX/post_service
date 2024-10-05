package faang.school.postservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("text-gears")
public class TextGearsProperties {
    private String baseUrl;
    private String correct;
    private String apiKey;
}
