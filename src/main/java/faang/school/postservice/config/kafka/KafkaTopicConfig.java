package faang.school.postservice.config.kafka;

import faang.school.postservice.config.properties.kafka.KafkaProperties;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class KafkaTopicConfig {

    private final KafkaProperties kafkaProperties;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG,
                kafkaProperties.getProducerConfig().getBootstrapServersConfig());
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic postsTopic() {
        return new NewTopic(kafkaProperties.getTopics().getPostCreatedTopic().getName(),
                kafkaProperties.getTopics().getPostCreatedTopic().getNumPartitions(),
                kafkaProperties.getTopics().getPostCreatedTopic().getReplicationFactor());
    }

    @Bean
    public NewTopic postLikeTopic() {
        return new NewTopic(kafkaProperties.getTopics().getPostLikeTopic().getName(),
                kafkaProperties.getTopics().getPostLikeTopic().getNumPartitions(),
                kafkaProperties.getTopics().getPostLikeTopic().getReplicationFactor());
    }

    @Bean
    public NewTopic commentLikeTopic() {
        return new NewTopic(kafkaProperties.getTopics().getCommentLikeTopic().getName(),
                kafkaProperties.getTopics().getCommentLikeTopic().getNumPartitions(),
                kafkaProperties.getTopics().getCommentLikeTopic().getReplicationFactor());
    }

    @Bean
    public NewTopic commentTopic() {
        return new NewTopic(kafkaProperties.getTopics().getCommentCreatedTopic().getName(),
                kafkaProperties.getTopics().getCommentCreatedTopic().getNumPartitions(),
                kafkaProperties.getTopics().getCommentCreatedTopic().getReplicationFactor());
    }
}
