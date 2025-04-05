package faang.school.postservice.config.kafka;

import faang.school.postservice.dto.feed.FeedHeaterEvent;
import faang.school.postservice.dto.post.PostCommentEvent;
import faang.school.postservice.dto.post.PostLikeEvent;
import faang.school.postservice.dto.post.PostProcessEvent;
import faang.school.postservice.dto.post.PostPublicationEvent;
import faang.school.postservice.dto.post.PostViewEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;


@Configuration
@RequiredArgsConstructor
public class KafkaConsumersConfig {
    private final KafkaProperties kafkaProperties;

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PostViewEvent> postViewEventContainerFactory(
            ConsumerFactory<String, PostViewEvent> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, PostViewEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PostLikeEvent> postLikeEventContainerFactory(
            ConsumerFactory<String, PostLikeEvent> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, PostLikeEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PostPublicationEvent> postPublishEventContainerFactory(
            ConsumerFactory<String, PostPublicationEvent> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, PostPublicationEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PostCommentEvent> postCommentEventContainerFactory(
            ConsumerFactory<String, PostCommentEvent> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, PostCommentEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PostProcessEvent> postProcessEventContainerFactory(
            ConsumerFactory<String, PostProcessEvent> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, PostProcessEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, FeedHeaterEvent> feedHeaterEventContainerFactory(
            ConsumerFactory<String, FeedHeaterEvent> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, FeedHeaterEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        return factory;
    }
}
