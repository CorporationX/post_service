package faang.school.postservice.config.kafka;

import faang.school.postservice.dto.feed.FeedHeaterEvent;
import faang.school.postservice.dto.post.PostCommentEvent;
import faang.school.postservice.dto.post.PostLikeEvent;
import faang.school.postservice.dto.post.PostProcessEvent;
import faang.school.postservice.dto.post.PostPublicationEvent;
import faang.school.postservice.dto.post.PostViewEvent;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
@RequiredArgsConstructor
public class KafkaProducerConfig {

    private final KafkaProperties kafkaProperties;

    private Map<String, Object> producerConfigs() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, kafkaProperties.getProducer().getKeySerializer());
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, kafkaProperties.getProducer().getValueSerializer());
        configProps.put(ProducerConfig.ACKS_CONFIG, kafkaProperties.getProducer().getAcks());
        configProps.put(ProducerConfig.RETRIES_CONFIG, kafkaProperties.getProducer().getRetries());
        return configProps;
    }

    @Bean
    public ProducerFactory<String, PostViewEvent> postViewEventProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public ProducerFactory<String, PostLikeEvent> postLikeEventProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public ProducerFactory<String, PostCommentEvent> postCommentEventProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public ProducerFactory<String, PostPublicationEvent> postPublishEventProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public ProducerFactory<String, PostProcessEvent> postProcessEventProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public ProducerFactory<String, FeedHeaterEvent> feedHeaterEventProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public KafkaTemplate<String, PostViewEvent> postViewEventKafkaTemplate(
            ProducerFactory<String, PostViewEvent> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public KafkaTemplate<String, PostLikeEvent> postLikeEventKafkaTemplate(
            ProducerFactory<String, PostLikeEvent> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public KafkaTemplate<String, PostPublicationEvent> postPublishEventKafkaTemplate(
            ProducerFactory<String, PostPublicationEvent> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public KafkaTemplate<String, PostCommentEvent> postCommentEventKafkaTemplate(
            ProducerFactory<String, PostCommentEvent> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public KafkaTemplate<String, PostProcessEvent> postProcessEventKafkaTemplate(
            ProducerFactory<String, PostProcessEvent> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public KafkaTemplate<String, FeedHeaterEvent> feedHeaterEventKafkaTemplate(
            ProducerFactory<String, FeedHeaterEvent> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }
}
