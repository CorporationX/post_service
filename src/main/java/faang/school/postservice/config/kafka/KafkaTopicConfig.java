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

    @Value(value = "${spring.data.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Value(value = "${spring.data.kafka.topic.post.name}")
    private String postTopic;

    @Value(value = "${spring.data.kafka.topic.post-views.name}")
    private String postViewTopic;

    @Value(value = "${spring.data.kafka.topic.comments.name}")
    private String commentCreatedTopic;

    @Value(value = "${spring.data.kafka.topic.post.partitions}")
    private int postTopicNumPartitions;

    @Value(value = "${spring.data.kafka.topic.post.replicas}")
    private short postReplicationFactor;

    @Value(value = "${spring.data.kafka.topic.comments.partitions}")
    private int commentTopicNumPartitions;

    @Value(value = "${spring.data.kafka.topic.comments.replicas}")
    private short commentReplicationFactor;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic postsTopic() {
        return new NewTopic(postTopic, postTopicNumPartitions, postReplicationFactor);
    }

    @Bean
    public NewTopic postViewTopic() {
        return new NewTopic(postViewTopic, postTopicNumPartitions, postReplicationFactor);
    }

    @Bean
    public NewTopic commentCreatedTopic() {
        return new NewTopic(commentCreatedTopic, commentTopicNumPartitions, commentReplicationFactor);
    }
}
