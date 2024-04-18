package faang.school.postservice.config.kafka;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {
    @Value("${kafka.partitions}")
    private int partitionsAmount;
    @Value("${kafka.replicas}")
    private int replicaAmount;
    @Value(value = "${kafka.bootstrap-servers}")
    private String bootstrapAddress;
    @Value("${kafka.topics.post_view.name}")
    private String topicPostView;
    @Value("${kafka.topics.post.name}")
    private String topicPost;
    @Value("${kafka.topics.comment.name}")
    private String topicComment;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic topicPostView() {
        return TopicBuilder
                .name(topicPostView)
                .partitions(partitionsAmount)
                .replicas(replicaAmount)
                .build();
    }

    @Bean
    public NewTopic topicPost() {
        return TopicBuilder
                .name(topicPost)
                .partitions(partitionsAmount)
                .replicas(replicaAmount)
                .build();
    }

    @Bean
    public NewTopic topicComment() {
        return TopicBuilder
                .name(topicComment)
                .partitions(partitionsAmount)
                .replicas(replicaAmount)
                .build();
    }
}