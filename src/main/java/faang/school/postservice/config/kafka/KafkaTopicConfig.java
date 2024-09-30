package faang.school.postservice.config.kafka;

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

    @Value("${spring.data.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Value("${spring.data.kafka.topic.like}")
    private String likeTopic;

    @Value("${spring.data.kafka.partition}")
    private int partition;

    @Value("${spring.data.kafka.replication_factor}")
    private short replicationFactor;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();

        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic likesTopic() {
        return new NewTopic(likeTopic, partition, replicationFactor);
    }
}
