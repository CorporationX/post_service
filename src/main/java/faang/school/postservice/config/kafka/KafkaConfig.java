package faang.school.postservice.config.kafka;


import faang.school.postservice.properties.kafka.Consumer;
import faang.school.postservice.properties.kafka.KafkaProperties;
import faang.school.postservice.properties.kafka.Producer;
import faang.school.postservice.properties.kafka.Topic;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ContainerProperties;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
@RequiredArgsConstructor
public class KafkaConfig {
    private final KafkaProperties properties;
    @PostConstruct
    public void testKafkaConnection() {
        try (AdminClient adminClient = AdminClient.create(Map.of(
                AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, properties.getBootstrapServers()))) {
            adminClient.describeCluster().nodes().get();
            System.out.println("Kafka brokers are reachable!");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Kafka brokers are not reachable. Please check configuration.");
        }
    }

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Producer producer = properties.getProducer();
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, properties.getBootstrapServers());
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.ACKS_CONFIG, producer.getAcks());
        configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, producer.isEnableIdempotence());
        configProps.put(ProducerConfig.RETRIES_CONFIG, producer.getRetries());
        configProps.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, producer.getMaxInFlightRequestsPerConnection());
        configProps.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, producer.getTransactionalId());
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate(ProducerFactory<String, String> producerFactory) {
        KafkaTemplate<String, String> template = new KafkaTemplate<>(producerFactory);
        template.setTransactionIdPrefix("payment-transaction-");
        return template;
    }

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Consumer consumer = properties.getConsumer();
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, properties.getBootstrapServers());
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, consumer.isEnableAutoCommit());
        configProps.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, consumer.getIsolationLevel());
        return new DefaultKafkaConsumerFactory<>(configProps);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(1);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        return factory;
    }

    @Bean
    public NewTopic publishPostTopic() {
        Topic topic = properties.getTopics().get("publish-post");
        return new NewTopic(topic.getName(), topic.getNumPartitions(), topic.getReplicationFactor());
    }
}
