package faang.school.postservice.config.kafka;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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

    @Value(value = "${spring.kafka.bootstrap-servers}")
    @NotNull(message = "Bootstrap address can not be null")
    @NotEmpty(message = "Bootstrap address can not be empty")
    private String bootstrapAddress;

    @Value(value = "${spring.kafka.topic.post.name}")
    @NotNull(message = "Topic post name can not be null")
    @NotEmpty(message = "Topic post name can not be empty")
    private String postName;

    @Value(value = "${spring.kafka.topic.post.partitions}")
    @NotNull(message = "Post partitions must be specified")
    @Min(value = 1, message = "Post partitions must be positive")
    private int postPartitions;

    @Value(value = "${spring.kafka.topic.post.replicationFactor}")
    @NotNull(message = "Post replication factor must be specified")
    @Min(value = 1, message = "Post replication factor must be positive")
    private short postReplicationFactor;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic posts() {
        return new NewTopic(
                postName,
                postPartitions,
                postReplicationFactor);
    }
}