package faang.school.postservice.config.corrector;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "corrector.grammar-bot")
public class GrammarBotConfig {
    private String spellCheckerUri;
    private String keyValue;
    private String keyHeader;
    private String contentTypeValue;
    private String contentTypeHeader;
}
