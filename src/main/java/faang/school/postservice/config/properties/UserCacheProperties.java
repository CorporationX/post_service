package faang.school.postservice.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.cache.users")
public class UserCacheProperties {

    private String keyPrefix = "users";
    private Duration ttl = Duration.ofDays(1);
}
