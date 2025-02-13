package faang.school.postservice.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    @Value(value = "${spring.kafka.topics.user-ban-topic.name}")
    private String userBanTopicName;

    @Value(value = "${spring.kafka.topics.user-ban-topic.num-partitions}")
    private int userBanTopicNumPartitions;

    @Value(value = "${spring.kafka.topics.user-ban-topic.replication-factor}")
    private short userBanTopicReplicationFactor;

    @Value("${spring.kafka.topics.notification-like-topic.name}")
    private String notificationLikeTopicName;

    @Value("${spring.kafka.topics.notification-like-topic.num-partitions}")
    private int notificationLikeTopicNumPartitions;

    @Value("${spring.kafka.topics.notification-like-topic.replication-factor}")
    private short notificationLikeTopicReplicationFactor;

    @Bean
    public NewTopic userBanTopic() {
        return new NewTopic(userBanTopicName, userBanTopicNumPartitions, userBanTopicReplicationFactor);
    }

    @Bean
    public NewTopic notificationLikeTopic() {
        return new NewTopic(notificationLikeTopicName, notificationLikeTopicNumPartitions, notificationLikeTopicReplicationFactor);
    }
}
