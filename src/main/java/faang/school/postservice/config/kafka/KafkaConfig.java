package faang.school.postservice.config.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.producer.client-id}")
    private String clientId;

    @Value("${app.kafka.topics.posts.name}")
    private String postsTopicName;

    @Value("${app.kafka.topics.posts.partitions}")
    private int postsTopicPartitions;

    @Value("${app.kafka.topics.posts.replication-factor}")
    private short postsTopicReplicationFactor;

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();

        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.CLIENT_ID_CONFIG, clientId);

        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        configProps.put(ProducerConfig.ACKS_CONFIG, "all");
        configProps.put(ProducerConfig.RETRIES_CONFIG, 3);
        configProps.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 1000);

        configProps.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        configProps.put(ProducerConfig.LINGER_MS_CONFIG, 5);
        configProps.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);

        configProps.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");

        configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);

        log.info("Kafka producer factory configured with bootstrap servers: {}", bootstrapServers);

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        KafkaTemplate<String, Object> template = new KafkaTemplate<>(producerFactory());

        template.setProducerListener(new org.springframework.kafka.support.ProducerListener<String, Object>() {
            public void onError(org.apache.kafka.clients.producer.ProducerRecord<String, Object> producerRecord,
                                org.apache.kafka.common.KafkaException exception) {
                log.error("Failed to send message to Kafka topic: {}, partition: {}, key: {}",
                        producerRecord.topic(), producerRecord.partition(), producerRecord.key(), exception);
            }

            @Override
            public void onSuccess(org.apache.kafka.clients.producer.ProducerRecord<String, Object> producerRecord,
                                  org.apache.kafka.clients.producer.RecordMetadata recordMetadata) {
                log.debug("Successfully sent message to Kafka topic: {}, partition: {}, offset: {}",
                        recordMetadata.topic(), recordMetadata.partition(), recordMetadata.offset());
            }
        });

        log.info("KafkaTemplate configured successfully");
        return template;
    }

    @Bean
    public NewTopic postsTopicConfig() {
        log.info("Creating Kafka topic: {} with {} partitions and replication factor: {}",
                postsTopicName, postsTopicPartitions, postsTopicReplicationFactor);

        return TopicBuilder.name(postsTopicName)
                .partitions(postsTopicPartitions)
                .replicas(postsTopicReplicationFactor)
                .config("cleanup.policy", "delete")
                .config("retention.ms", "604800000")
                .config("segment.ms", "86400000")
                .build();
    }
}