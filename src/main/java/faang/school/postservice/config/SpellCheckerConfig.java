package faang.school.postservice.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpellCheckerConfig {

    @Value("${post-correcter.url}")
    private String url;

    @Value("${post-correcter.text-param}")
    private String textParam;

    @Value("${post-correcter.key-param}")
    private String keyParam;

    @Value("${post-correcter.key}")
    private String key;

    @Value("${post-correcter.language-param}")
    private String languageParam;

    @Value("${post-correcter.language}")
    private String language;
}
