package faang.school.postservice.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@Data
@ConfigurationProperties(prefix = "spring.data")
public class ChannelProperty {

    private Map<String, Channel> channels;

    @Data
    public static class Channel {

        private String name;
        private Integer partition;
        private Short replication;
    }
}
