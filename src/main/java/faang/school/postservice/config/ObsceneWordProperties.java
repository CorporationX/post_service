package faang.school.postservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Set;

@Data
@ConfigurationProperties("censorship")
public class ObsceneWordProperties {
    private Set<String> words;
}
