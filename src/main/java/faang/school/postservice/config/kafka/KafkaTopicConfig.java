package faang.school.postservice.config.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class KafkaTopicConfig {

    private final KafkaTopicProperties kafkaTopicProperties;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaTopicProperties.getBootstrapServers());
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic feedHeatEventTopic() {
        return TopicBuilder.name(kafkaTopicProperties.getTopics().getFeedHeatEvent())
                .partitions(kafkaTopicProperties.getVariables().getPartitions())
                .replicas(kafkaTopicProperties.getVariables().getReplicas())
                .build();
    }

    @Bean
    public NewTopic likeEventTopic() {
        return TopicBuilder.name(kafkaTopicProperties.getTopics().getLikeEvent())
                .partitions(kafkaTopicProperties.getVariables().getPartitions())
                .replicas(kafkaTopicProperties.getVariables().getReplicas())
                .build();
    }

    @Bean
    public NewTopic commentEventTopic() {
        return TopicBuilder.name(kafkaTopicProperties.getTopics().getCommentEvent())
                .partitions(kafkaTopicProperties.getVariables().getPartitions())
                .replicas(kafkaTopicProperties.getVariables().getReplicas())
                .build();
    }

    @Bean
    public NewTopic postEventTopic() {
        return TopicBuilder.name(kafkaTopicProperties.getTopics().getPostEvent())
                .partitions(kafkaTopicProperties.getVariables().getPartitions())
                .replicas(kafkaTopicProperties.getVariables().getReplicas())
                .build();
    }

    @Bean
    public NewTopic postViewedEventTopic() {
        return TopicBuilder.name(kafkaTopicProperties.getTopics().getPostViewedEvent())
                .partitions(kafkaTopicProperties.getVariables().getPartitions())
                .replicas(kafkaTopicProperties.getVariables().getReplicas())
                .build();
    }
}