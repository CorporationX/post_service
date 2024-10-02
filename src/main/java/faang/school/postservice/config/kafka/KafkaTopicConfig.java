package faang.school.postservice.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    @Value("${spring.kafka.topics.post.name}")
    private String postTopicName;
    @Value("${spring.kafka.topics.post.partitions}")
    private int postTopicPartitions;
    @Value("${spring.kafka.topics.post.replication-factor}")
    private short postTopicReplicationFactor;

    @Bean
    public NewTopic postTopic() {
        return new NewTopic(
                postTopicName,
                postTopicPartitions,
                postTopicReplicationFactor
        );
    }

    @Value("${spring.kafka.topics.like-post.name}")
    private String likePostTopicName;
    @Value("${spring.kafka.topics.like-post.partitions}")
    private int likePostTopicPartitions;
    @Value("${spring.kafka.topics.like-post.replication-factor}")
    private short likePostTopicReplicationFactor;

    @Bean
    public NewTopic likePostTopic() {
        return new NewTopic(
                likePostTopicName,
                likePostTopicPartitions,
                likePostTopicReplicationFactor
        );
    }

    @Value("${spring.kafka.topics.comment.name}")
    private String commentTopicName;
    @Value("${spring.kafka.topics.comment.partitions}")
    private int commentTopicPartitions;
    @Value("${spring.kafka.topics.comment.replication-factor}")
    private short commentTopicReplicationFactor;

    @Bean
    public NewTopic commentTopic() {
        return new NewTopic(
                commentTopicName,
                commentTopicPartitions,
                commentTopicReplicationFactor
        );
    }

    @Value("${spring.kafka.topics.post-views.name}")
    private String postViewTopicName;
    @Value("${spring.kafka.topics.post-views.partitions}")
    private int postViewTopicPartitions;
    @Value("${spring.kafka.topics.post-views.replication-factor}")
    private short postViewTopicReplicationFactor;

    @Bean
    public NewTopic postViewTopic() {
        return new NewTopic(
                postViewTopicName,
                postViewTopicPartitions,
                postViewTopicReplicationFactor
        );
    }
}
