package faang.school.postservice.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@Data
@ConfigurationProperties(prefix = "spring.data.kafka.topics")
public class KafkaChannelProperty {

    private Short defaultReplication;
    private Integer defaultPartition;
    private Map<String, Channel> topicSettings;

    @Data
    public static class Channel {

        private String name;
        private Integer partition;
        private Short replication;
    }
}
