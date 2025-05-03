package faang.school.postservice.config.kafka.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.kafka.topic.posts")
public record PostsTopicProperties(
        String name,
        int partitionCount
) {
}
