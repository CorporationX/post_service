package faang.school.postservice.config.redis;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "spring.data.redis" )
public class RedisProperties {
    @NotBlank(message = "Redis host cannot be blank or null")
    private String host;
    @Positive(message = "Redis port must be positive")
    private int port;
    @Positive(message = "Redis connect timeout must be positive")
    private Long connectTimeout;
    @Positive(message = "Redis fixed timeout must be positive")
    private Long timeout;


    private Lettuce lettuce = new Lettuce();

    @Getter
    @Setter
    public static class Lettuce {
        private Pool pool = new Pool();
    }

    @Getter
    @Setter
    public static class Pool {

        @Positive(message = "Max active connections must be positive")
        private int maxActive;

        @Positive(message = "Max idle connections must be positive")
        private int maxIdle;

        @PositiveOrZero(message = "Min idle connections cannot be negative")
        private int minIdle;

        @Positive
        private int maxWait;
    }
}
