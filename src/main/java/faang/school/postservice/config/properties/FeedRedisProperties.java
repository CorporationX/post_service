package faang.school.postservice.config.properties;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "spring.data.redis")
public class FeedRedisProperties {

    @NotBlank
    private String host;

    @NotNull
    @Min(1)
    @Max(65535)
    private Integer port;

    @NotNull
    @Min(100)
    private Long timeout;

    @NotNull
    private LettucePool lettucePool = new LettucePool();

    @Getter
    @Setter
    @Validated
    public static class LettucePool {

        @NotNull
        @Min(1)
        private Integer maxTotal;

        @NotNull
        @Min(0)
        private Integer maxIdle;

        @NotNull
        @Min(0)
        private Integer minIdle;

        @NotNull
        @Min(0)
        private Integer maxWait;
    }
}
