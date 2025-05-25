package faang.school.postservice.config.kafka;

import faang.school.postservice.config.properties.HashtagAddingTopicProperties;
import faang.school.postservice.config.properties.HashtagRemovingTopicProperties;
import faang.school.postservice.config.properties.LikesTopicProperties;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@RequiredArgsConstructor
public class KafkaTopicsConfig {

    private final HashtagAddingTopicProperties hashtagAddingTopic;
    private final HashtagRemovingTopicProperties hashtagRemovingTopic;
    private final LikesTopicProperties likesTopicProperties;

    @Bean
    public NewTopic hashtagAddingTopic() {
        return createTopic(hashtagAddingTopic.name(),
                hashtagAddingTopic.partitions(),
                hashtagAddingTopic.replicas());
    }

    @Bean
    public NewTopic hashtagRemovingTopic() {
        return createTopic(hashtagRemovingTopic.name(),
                hashtagRemovingTopic.partitions(),
                hashtagRemovingTopic.replicas());
    }

    @Bean
    public NewTopic likesTopic() {
        return createTopic(likesTopicProperties.name(),
                likesTopicProperties.partitions(),
                likesTopicProperties.replicas());
    }

    private NewTopic createTopic(String name, int partitions, int replicas) {
        return TopicBuilder.name(name)
                .partitions(partitions)
                .replicas(replicas)
                .build();
    }
}
