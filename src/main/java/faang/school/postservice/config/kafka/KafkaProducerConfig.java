package faang.school.postservice.config.kafka;

import faang.school.postservice.dto.kafka.PostViewEvent;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class KafkaProducerConfig {

    @Value("${kafka.host}")
    private String bootstrapAddress;

    @Value("${kafka.topics.like-post}")
    private String likePostTopic;

    @Value("${spring.kafka.topic.post-views}")
    private String postViewsTopic;

    @Value("${kafka.num-partitions}")
    private int numPartitions;

    @Value("${kafka.replication-factor}")
    private int replicationFactor;

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public ProducerFactory<String, PostViewEvent> postViewEventProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public KafkaTemplate<String, PostViewEvent> postViewEventKafkaTemplate() {
        return new KafkaTemplate<>(postViewEventProducerFactory());
    }

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        configs.put(AdminClientConfig.RETRIES_CONFIG, 5);
        configs.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, 30000);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic likePostTopic() {
        return TopicBuilder.name(likePostTopic)
                .partitions(numPartitions)
                .replicas(replicationFactor)
                .build();
    }

    @Bean
    public NewTopic postViewsTopic() {
        int partitions = numPartitions > 0 ? numPartitions : 3;
        int replicas = replicationFactor > 0 ? replicationFactor : 1;

        return TopicBuilder.name(postViewsTopic)
                .partitions(partitions)
                .replicas(replicas)
                .build();
    }
}