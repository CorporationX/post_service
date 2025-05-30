package faang.school.postservice.config.newsfeed.kafka;

import faang.school.postservice.config.properties.KafkaProperties;
import lombok.RequiredArgsConstructor;
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
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
@RequiredArgsConstructor
public class KafkaConfig {

    private final KafkaProperties props;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, props.getBootstrapServers());
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic postsTopic() {
        return createDefaultTopic(props.getTopic().getPosts());
    }

    @Bean
    public NewTopic postViewsTopic() {
        return createDefaultTopic(props.getTopic().getPostViews());
    }

    @Bean
    public NewTopic likesTopic() {
        return createDefaultTopic(props.getTopic().getLikes());
    }

    @Bean
    public NewTopic commentsTopic() {
        return createDefaultTopic(props.getTopic().getComments());
    }

    private NewTopic createDefaultTopic(String topicName) {
        KafkaProperties.Topic topicProps = props.getTopic();
        return TopicBuilder.name(topicName)
                .partitions(topicProps.getPartitions())
                .replicas(topicProps.getReplicas())
                .config("retention.ms", topicProps.getRetentionMs())
                .config("cleanup.policy", topicProps.getCleanupPolicy())
                .config("min.insync.replicas", topicProps.getMinInSyncReplicas())
                .config("unclean.leader.election.enable", topicProps.getUncleanLeaderElection())
                .build();
    }

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        KafkaProperties.Producer producerProps = props.getProducer();
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, props.getBootstrapServers());
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        configProps.put(ProducerConfig.ACKS_CONFIG, producerProps.getAcks());
        configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, producerProps.getIdempotence());
        configProps.put(ProducerConfig.RETRIES_CONFIG, producerProps.getRetries());
        configProps.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, producerProps.getMaxInFlightRequests());
        configProps.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, producerProps.getCompressionType());
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        KafkaProperties.Consumer consumerProps = props.getConsumer();
        JsonDeserializer<Object> jsonDeserializer = new JsonDeserializer<>();
        jsonDeserializer.addTrustedPackages("*");
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, props.getBootstrapServers());
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, consumerProps.getGroupId());
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        configProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, consumerProps.getEnableAutoCommit());
        configProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, consumerProps.getAutoOffsetReset());
        return new DefaultKafkaConsumerFactory<>(configProps, new StringDeserializer(), jsonDeserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        return factory;
    }
}
