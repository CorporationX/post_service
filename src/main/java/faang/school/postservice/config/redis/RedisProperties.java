package faang.school.postservice.config.redis;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@ConfigurationProperties("spring.data.redis")
@Configuration
public class RedisProperties {
    @NotBlank(message = "Redis host not be null")
    private String host;

    @Positive(message = "Redis port cannot be negative")
    private int port;

    @NotBlank(message = "Redis password cannot be null or empty")
    private String password;

    @NotBlank(message = "Redis userBanTopic must not be null or empty")
    private String userBanTopic;

    @Positive(message = "Posts TTL must be positive")
    private long postsTtlSeconds;

    @NotBlank(message = "Posts key prefix must not be empty")
    private String postsKeyPrefix;
}