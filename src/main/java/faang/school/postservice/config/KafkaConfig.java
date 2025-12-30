package faang.school.postservice.config;

import faang.school.postservice.dto.event.FeedHeatEvent;
import faang.school.postservice.dto.event.PostViewEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ConsumerFactory<String, PostViewEvent> postViewEventConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "post-service-group");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 100);
        props.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, 1024);
        props.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, 500);

        JsonDeserializer<PostViewEvent> deserializer = new JsonDeserializer<>(PostViewEvent.class);
        deserializer.addTrustedPackages("*");
        deserializer.setRemoveTypeHeaders(false);
        deserializer.setUseTypeMapperForKey(false);

        return new DefaultKafkaConsumerFactory<>(
            props,
            new StringDeserializer(),
            deserializer
        );
    }

    /**
     * Consumer Factory для FeedHeatEvent.
     */
    @Bean
    public ConsumerFactory<String, FeedHeatEvent> feedHeatEventConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "feed-heat-group");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 10);

        JsonDeserializer<FeedHeatEvent> deserializer = new JsonDeserializer<>(FeedHeatEvent.class);
        deserializer.addTrustedPackages("*");
        deserializer.setRemoveTypeHeaders(false);
        deserializer.setUseTypeMapperForKey(false);

        return new DefaultKafkaConsumerFactory<>(
            props,
            new StringDeserializer(),
            deserializer
        );
    }

    /**
     * Kafka Listener Container Factory с поддержкой DLQ.
     * Использует готовый KafkaTemplate<Object, Object> из Spring Boot auto-configuration.
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PostViewEvent> kafkaListenerContainerFactory(
            ConsumerFactory<String, PostViewEvent> consumerFactory,
            KafkaTemplate<Object, Object> kafkaTemplate) {
        
        ConcurrentKafkaListenerContainerFactory<String, PostViewEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setConcurrency(3);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);

        factory.setCommonErrorHandler(new DefaultErrorHandler(
            new DeadLetterPublishingRecoverer(kafkaTemplate,
                (record, ex) -> new TopicPartition("post-views-dlq", -1)),
            new FixedBackOff(2000L, 3L)
        ));
        
        return factory;
    }

    /**
     * Kafka Listener Container Factory для FeedHeatEvent.
     * Использует больше потоков для параллельной обработки батчей.
     * Добавлен error handler с DLQ для обработки ошибок.
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, FeedHeatEvent> feedHeatListenerContainerFactory(
            ConsumerFactory<String, FeedHeatEvent> consumerFactory,
            KafkaTemplate<Object, Object> kafkaTemplate) {
        
        ConcurrentKafkaListenerContainerFactory<String, FeedHeatEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setConcurrency(5);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        
        // Error handling с DLQ: 2 попытки с задержкой 5 сек, затем в DLQ
        factory.setCommonErrorHandler(new DefaultErrorHandler(
            new DeadLetterPublishingRecoverer(kafkaTemplate,
                (record, ex) -> {
                    log.error("Sending feed-heat batch to DLQ: partition={}, offset={}", 
                        record.partition(), record.offset());
                    return new TopicPartition("feed-heat-dlq", -1);
                }),
            new FixedBackOff(5000L, 2L)
        ));
        
        return factory;
    }
}

