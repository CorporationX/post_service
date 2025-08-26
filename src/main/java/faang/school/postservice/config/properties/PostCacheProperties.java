package faang.school.postservice.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.cache.posts")
public class PostCacheProperties {

    private String keyPrefix = "posts";
    private Duration ttl = Duration.ofDays(1);
}
