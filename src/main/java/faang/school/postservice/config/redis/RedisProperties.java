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
    @NotBlank(message = "Redis host cannot be blank")
    private String host;
    @Positive(message = "Redis port must be positive")
    private int port;
    @NotBlank(message = "Redis password cannot be blank")
    private String password;
    @NotBlank(message = "Redis userBanTopic cannot be blank")
    private String userBanTopic;
    @NotBlank(message = "Redis commentTopic cannot be blank")
    private String commentTopic;
}
