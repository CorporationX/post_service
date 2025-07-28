package faang.school.postservice.kafka.producer;

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
public class KafkaTopicsConfig {
    @Value("${spring.kafka.producer.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        KafkaAdmin admin = new KafkaAdmin(configs);
        admin.setAutoCreate(true);
        return admin;
    }

    @Bean
    public NewTopic commentCreatedTopic(
            @Value("${spring.kafka.topics.comment-created.name}") String name,
            @Value("${spring.kafka.topics.comment-created.partitions}") int partitions,
            @Value("${spring.kafka.topics.comment-created.replication-factor}") short replicas
    ) {
        return TopicBuilder
                .name(name)
                .partitions(partitions)
                .replicas(replicas)
                .build();
    }

    @Bean
    public NewTopic commentCreatedDlt(
            @Value("${spring.kafka.topics.comment-created.dlt.name}") String name,
            @Value("${spring.kafka.topics.comment-created.dlt.partitions}") int partitions,
            @Value("${spring.kafka.topics.comment-created.dlt.replication-factor}") short replicas
    ) {
        return TopicBuilder.name(name)
                .partitions(partitions)
                .replicas(replicas)
                .build();
    }
}
