package faang.school.postservice.config.properties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@Component
@ConfigurationProperties(prefix ="spring.data.redis.health-checker")
public class RedisReconnectionProperties {

    @NotNull
    @PositiveOrZero
    private Integer minCacheKeys;

    @NotNull
    @Positive
    private Integer redisUnavailableTtlSec;

    @NotNull
    @Positive
    private Integer reconnectionIntervalSec;

    @NotNull
    @PositiveOrZero
    private Integer initialDelaySec;

    @NotBlank
    private String redisUnavailableKey;

    @NotNull
    private Retry retry;

    @Getter
    @Setter
    @Validated
    public static class Retry {

        @NotNull
        @Positive
        private Integer delayMs;

        @NotNull
        @Positive
        private Integer multiplier;
    }

    @NotNull
    private HealthCheckScheduler healthCheckScheduler;

    @Getter
    @Setter
    @Validated
    public static class HealthCheckScheduler {

        @NotNull
        @Positive
        private Integer fixedRateMs;
    }
}
