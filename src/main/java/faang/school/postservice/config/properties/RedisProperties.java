package faang.school.postservice.config.properties;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "spring.data.redis")
public class RedisProperties {

    @NotBlank
    private String host;

    private Integer port;

    private Channels channels;

    @Getter
    @Setter
    public static class Channels {
        @NotBlank
        private String comment;

        private Channel calculationsChannel;

        @Getter
        @Setter
        public static class Channel {
            private String name;
        }
    }
}
