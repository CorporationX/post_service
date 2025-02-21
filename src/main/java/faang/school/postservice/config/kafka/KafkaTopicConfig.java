package faang.school.postservice.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    @Value("${spring.kafka.topics.notification-like-topic.name}")
    private String notificationLikeTopicName;

    @Value("${spring.kafka.topics.notification-like-topic.num-partitions}")
    private int notificationLikeTopicNumPartitions;

    @Value("${spring.kafka.topics.notification-like-topic.replication-factor}")
    private short notificationLikeTopicReplicationFactor;

    @Bean
    public NewTopic notificationLikeTopic() {
        return new NewTopic(notificationLikeTopicName, notificationLikeTopicNumPartitions, notificationLikeTopicReplicationFactor);
    }
}