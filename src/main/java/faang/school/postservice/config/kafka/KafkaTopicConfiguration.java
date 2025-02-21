package faang.school.postservice.config.kafka;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaTopicConfiguration {

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;
    @Value(value = "${spring.kafka.topic.comment.name}")
    private String commentsTopic;
    @Value(value = "${spring.kafka.topic.comment.partitions}")
    private int commentTopicNumPartitions;
    @Value(value = "${spring.kafka.topic.comment.replicationFactor}")
    private short commentReplicationFactor;
    @Value(value = "${spring.kafka.available.brokers}")
    private int availableBrokers;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic commentsTopic() {
        short replicationFactor = (availableBrokers > 1) ? commentReplicationFactor : 1;
        return new NewTopic(commentsTopic, commentTopicNumPartitions, replicationFactor);
    }
}
