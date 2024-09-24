package faang.school.postservice.config.kafka;

import faang.school.postservice.properties.KafkaTopicsProperties;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class KafkaTopicConfig {
    private final KafkaTopicsProperties kafkaTopicsProperties;

    @Value("${spring.kafka.topics.likes.name}")
    private String likesName;

    @Value("${spring.kafka.topics.comments.name}")
    private String commentsName;

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();

        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic likesTopic() {
        KafkaTopicsProperties.Topic likesTopic = kafkaTopicsProperties.getTopics().get(likesName);
        return new NewTopic(likesTopic.getName(), likesTopic.getPartitions(), likesTopic.getReplicationFactor());
    }

    @Bean
    public NewTopic commentsTopic() {
        KafkaTopicsProperties.Topic commentsTopic = kafkaTopicsProperties.getTopics().get(commentsName);
        return new NewTopic(commentsTopic.getName(), commentsTopic.getPartitions(), commentsTopic.getReplicationFactor());
    }
}
