package faang.school.postservice.config.kafka;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.Map;

@Configuration
public class KafkaTopicsConfig {

    @Bean
    public KafkaAdmin kafkaAdmin(
            @Value("${kafka.bootstrap-servers}") String bootstrapServers) {
        return new KafkaAdmin(Map.of(
                AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers
        ));
    }

    @Bean
    public NewTopic likesTopic(
            @Value("${kafka.topics.likes.name}") String topicName,
            @Value("${kafka.topics.likes.partitions}") int partitions,
            @Value("${kafka.topics.likes.replication-factor}") short replicationFactor) {

        return TopicBuilder.name(topicName)
                .partitions(partitions)
                .replicas(replicationFactor)
                .compact()
                .build();
    }
}
