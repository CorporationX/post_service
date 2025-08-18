package faang.school.postservice.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.feed.heater")
public class FeedHeaterProperties {

    private int threads = 8;
    private int perUserPostLimit = 500;
}
