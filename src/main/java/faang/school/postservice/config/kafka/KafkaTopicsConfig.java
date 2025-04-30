package faang.school.postservice.config.kafka;

import faang.school.postservice.config.properties.HashtagAddingTopicProperties;
import faang.school.postservice.config.properties.HashtagRemovingTopicProperties;
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

    @Bean
    public NewTopic hashtagAddingTopic() {
        return createTopic(hashtagAddingTopic.getName(),
                hashtagAddingTopic.getPartitions(),
                hashtagAddingTopic.getReplicas());
    }

    @Bean
    public NewTopic hashtagRemovingTopic() {
        return createTopic(hashtagRemovingTopic.getName(),
                hashtagRemovingTopic.getPartitions(),
                hashtagRemovingTopic.getReplicas());
    }

    private NewTopic createTopic(String name, int partitions, int replicas) {
        return TopicBuilder.name(name)
                .partitions(partitions)
                .replicas(replicas)
                .build();
    }
}
