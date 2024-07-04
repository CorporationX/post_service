package faang.school.postservice.config.corrector;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "corrector.grammar-bot")
public class GrammarBotProperties {
    private String spellCheckerUri;
    private String keyValue;
    private String keyHeader;
    private String contentTypeValue;
    private String contentTypeHeader;
}
