package faang.school.postservice.config.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
@RequiredArgsConstructor
public class KafkaConfig {
    private final Environment environment;

    @Bean
    ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                environment.getProperty("spring.kafka.producer.bootstrap-servers"));
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        config.put(ProducerConfig.ACKS_CONFIG, environment.getRequiredProperty("spring.kafka.producer.acks"));
        config.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG,
                environment.getProperty("spring.kafka.producer.properties.delivery-timeout-ms"));
        config.put(ProducerConfig.LINGER_MS_CONFIG,
                environment.getProperty("spring.kafka.producer.properties.linger-ms"));
        config.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG,
                environment.getProperty("spring.kafka.producer.properties.request-timeout-ms"));
        config.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG,
                environment.getProperty("spring.kafka.producer.properties.idempotence"));
        config.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION,
                environment.getProperty("spring.kafka.producer.properties.max-in-flight-requests-per-connection"));
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    ConsumerFactory<String, Object> consumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                environment.getProperty("spring.kafka.consumer.bootstrap-servers"));
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        config.put(JsonDeserializer.TRUSTED_PACKAGES,
                environment.getProperty("spring.kafka.consumer.properties.spring.json.trusted.packages"));
        config.put(ConsumerConfig.GROUP_ID_CONFIG, environment.getProperty("spring.kafka.consumer.group-id"));

        return new DefaultKafkaConsumerFactory<>(config);
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory(
            ConsumerFactory<String, Object> consumerFactory
    ) {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        return factory;
    }

    @Bean
    public DeadLetterPublishingRecoverer deadLetterRecoverer(KafkaTemplate<?, ?> template) {
        return new DeadLetterPublishingRecoverer(template);
    }

    @Bean
    NewTopic postKafkaTopic() {
        return topicBuilder(environment.getRequiredProperty("spring.kafka.topics.post-published"));
    }

    @Bean
    NewTopic postViewKafkaTopic() {
        return topicBuilder(environment.getRequiredProperty("spring.kafka.topics.post-view"));
    }

    @Bean
    NewTopic postCommentKafkaTopic() {
        return topicBuilder(environment.getRequiredProperty("spring.kafka.topics.post-comment"));
    }

    @Bean
    NewTopic postLikeKafkaTopic(){
        return topicBuilder(environment.getRequiredProperty("spring.kafka.topics.post-like"));
    }

    private NewTopic topicBuilder(String topicName) {
        return TopicBuilder
                .name(topicName)
                .partitions(Integer.parseInt(environment.getRequiredProperty("spring.kafka.topics.partitions")))
                .replicas(Integer.parseInt(environment.getRequiredProperty("spring.kafka.topics.replicasCount")))
                .configs(Map.of(
                        "min.insync.replicas",
                        environment.getRequiredProperty("spring.kafka.topics.min-insync-replicas")))
                .build();
    }
}
