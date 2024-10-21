package faang.school.postservice.config.redis;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.redis.listener.ChannelTopic;

import java.util.Map;

@Data
@ConfigurationProperties(prefix = "spring.data.redis")
public class RedisProperties {
    private int port;
    private String host;
    private Map<String, String> channels;
    private ChannelTopic commentChannel;

    public String getChannel(String channelKey) {
        return channels.get(channelKey);
    }

    @PostConstruct
    public void init() {
        this.commentChannel = new ChannelTopic(getChannel("comment-channel"));
    }
}
