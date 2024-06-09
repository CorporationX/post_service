package faang.school.postservice.config.kafka;

import lombok.Getter;
import lombok.Setter;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Setter
@Getter
@ConfigurationProperties(prefix = "spring.data.kafka.topics")
public class KafkaTopicConfig {

    @Value("${spring.data.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    private String topicPost;
    private int partitionsPost;
    private int replicationPost;

    private String topicPostView;
    private int partitionsPostView;
    private int replicationPostView;

    private String topicLike;
    private int partitionsLike;
    private int replicationLike;

    private String topicComment;
    private int partitionsComment;
    private int replicationComment;

    private String topicFeedHeat;
    private int partitionsFeedHeat;
    private int replicationFeedHeat;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic topicPost() {
        return new NewTopic(topicPost, partitionsPost, (short) replicationPost);
    }

    @Bean
    public NewTopic topicPostView() {
        return new NewTopic(topicPostView, partitionsPostView, (short) replicationPostView);
    }

    @Bean
    public NewTopic topicLike() {
        return new NewTopic(topicLike, partitionsLike, (short) replicationLike);
    }

    @Bean
    public NewTopic topicComment() {
        return new NewTopic(topicComment, partitionsComment, (short) partitionsComment);
    }

    @Bean
    public NewTopic topicFeedHeat() {
        return new NewTopic(topicFeedHeat, partitionsFeedHeat, (short) replicationFeedHeat);
    }

}