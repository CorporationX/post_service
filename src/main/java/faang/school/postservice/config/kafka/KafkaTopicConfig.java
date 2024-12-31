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
    private final KafkaProperties kafkaProperties;

    @Bean
    public KafkaAdmin admin() {
        String bootstrapServer = kafkaProperties.getBootstrapServer();

        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);

        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic postTopic() {
        return TopicBuilder.name(kafkaProperties.getPostsTopic())
                .partitions(kafkaProperties.getPartition())
                .replicas(kafkaProperties.getReplica())
                .compact()
                .build();
    }

    @Bean
    public NewTopic postViewsTopic() {
        return TopicBuilder.name(kafkaProperties.getPostViewsTopic())
                .partitions(kafkaProperties.getPartition())
                .replicas(kafkaProperties.getReplica())
                .compact()
                .build();
    }

    @Bean
    public NewTopic commentsTopic() {
        return TopicBuilder.name(kafkaProperties.getCommentsTopic())
                .partitions(kafkaProperties.getPartition())
                .replicas(kafkaProperties.getReplica())
                .compact()
                .build();
    }

    @Bean
    public NewTopic heatFeedsTopic() {
        return TopicBuilder.name(kafkaProperties.getHeatFeedsTopic())
                .partitions(kafkaProperties.getPartition())
                .replicas(kafkaProperties.getReplica())
                .compact()
                .build();
    }

    @Bean
    public NewTopic heatPostsTopic() {
        return TopicBuilder.name(kafkaProperties.getHeatPostsTopic())
                .partitions(kafkaProperties.getPartition())
                .replicas(kafkaProperties.getReplica())
                .compact()
                .build();
    }
}