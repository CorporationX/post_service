package faang.school.postservice.config.api;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(value = "user-service")
public class UserService extends ApiProperties {
}
