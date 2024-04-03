package faang.school.postservice.config.kafka;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaTopicConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;
    @Value("${spring.kafka.topics.post.name}")
    private String postTopicName;
    @Value("${spring.kafka.topics.post.partitions}")
    private int postTopicPartitions;
    @Value("${spring.kafka.topics.post.replicas}")
    private short postTopicReplicas;

    @Value("${spring.kafka.topics.like.name}")
    private String likeTopicName;
    @Value("${spring.kafka.topics.like.partitions}")
    private int likeTopicPartitions;
    @Value("${spring.kafka.topics.like.replicas}")
    private short likeTopicReplicas;

    @Value("${spring.kafka.topics.comment.name}")
    private String commentTopicName;
    @Value("${spring.kafka.topics.comment.partitions}")
    private int commentTopicPartitions;
    @Value("${spring.kafka.topics.comment.replicas}")
    private short commentTopicReplicas;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic kafkaPostTopic() {
        return new NewTopic(postTopicName, postTopicPartitions, postTopicReplicas);
    }

    @Bean
    public NewTopic kafkaLikeTopic() {
        return new NewTopic(likeTopicName, likeTopicPartitions, likeTopicReplicas);
    }

    @Bean
    public NewTopic kafkaCommentTopic() {
        return new NewTopic(commentTopicName, commentTopicPartitions, commentTopicReplicas);

    }
}