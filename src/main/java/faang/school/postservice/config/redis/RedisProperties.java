package faang.school.postservice.config.redis;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "spring.data.redis")
public class RedisProperties {

    private Channels channels;

    @Getter
    @Setter
    protected static class Channels {
        private Channel calculationsChannel;
        private Channel likePostChannel;
        private Channel postViewChannel;

        @Getter
        @Setter
        protected static class Channel {
            private String name;
        }
    }
}
