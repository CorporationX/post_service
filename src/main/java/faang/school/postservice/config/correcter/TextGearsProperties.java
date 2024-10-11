package faang.school.postservice.config.correcter;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("text-gears")
public class TextGearsProperties {

    private String baseUrl;
    private String pathSegment;
    private String apiKey;
    private String languageCode;
}
