package faang.school.postservice.config.kafka;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.data.kafka")
public record KafkaProperties(
        boolean active,
        String connection,
        String groupId,
        String offset,
        int concurrency,
        int poolSize,
        ChannelNames topicNames
) {
    public record ChannelNames(
            String comments,
            String postView,
            String posts
    ) {
    }
}
