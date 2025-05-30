package faang.school.postservice.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.data.kafka.topic.hashtag-removing")
public record HashtagRemovingTopicProperties(
        String name,
        int partitions,
        int replicas
) {
}
