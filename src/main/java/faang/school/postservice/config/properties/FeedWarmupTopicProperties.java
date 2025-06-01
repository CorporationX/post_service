package faang.school.postservice.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.data.kafka.topic.feed-warmup")
public record FeedWarmupTopicProperties(
        String name,
        int partitions,
        int replicas
) {}
