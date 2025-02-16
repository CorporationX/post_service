package faang.school.postservice.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    @Value("${spring.kafka.topics.analytics-comment-topic.name}")
    private String analyticsCommentTopicName;

    @Value("${spring.kafka.topics.analytics-comment-topic.num-partitions}")
    private int analyticsCommentTopicNumPartitions;

    @Value("${spring.kafka.topics.analytics-comment-topic.replication-factor}")
    private short analyticsCommentTopicReplicationFactor;

    @Bean
    public NewTopic analyticsCommentTopic() {
        return new NewTopic(analyticsCommentTopicName, analyticsCommentTopicNumPartitions, analyticsCommentTopicReplicationFactor);
    }
}
