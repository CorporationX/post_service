package faang.school.postservice.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "kafka")
@Component
@Getter
@Setter
public class KafkaProperties {
    private String bootstrapServers;
    private String groupId;
    private String topic = "connectivity_check";
    private Integer maxPoll;
    private Topics topics;

    @Getter
    @Setter
    public static class Topics {
        private String postCreated;
        private String likes;
        private String comments;
    }
}
