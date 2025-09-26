package faang.school.postservice.config.kafka;

import faang.school.postservice.config.properties.kafka.KafkaTopicsProperties;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Value("${spring.kafka.feed-warmer-partitions}")
    private int feedWarmerPartitionCount;

    @Value("${spring.kafka.feed-warmer-replicas}")
    private int feedWarmerReplicaCount;



    @Bean
    public NewTopic createCommentTopic(KafkaTopicsProperties topics) {
        return TopicBuilder.name(topics.comment()).build();
    }

    @Bean
    public NewTopic createPostPublishedTopic(KafkaTopicsProperties topics) {
        return TopicBuilder.name(topics.post_published()).build();
    }

    @Bean
    public NewTopic createFeedWarmer(KafkaTopicsProperties topics) {
        return TopicBuilder.name(topics.feed_warmer())
                .partitions(feedWarmerPartitionCount)
                .replicas(feedWarmerReplicaCount)
                .build();
    }
}
