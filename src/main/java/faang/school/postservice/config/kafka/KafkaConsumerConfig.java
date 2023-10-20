package faang.school.postservice.config.kafka;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.post.PostEvent;
import faang.school.postservice.dto.post.PostViewEvent;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return props;
    }

    @Bean
    public ConsumerFactory<String, PostEvent> consumerFactoryPostEvent() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs(),
                new StringDeserializer(), new JsonDeserializer<>(PostEvent.class, false));
    }

    @Bean
    public ConsumerFactory<String, LikeDto> consumerFactoryLikeEvent() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs(),
                new StringDeserializer(), new JsonDeserializer<>(LikeDto.class, false));
    }

    @Bean
    public ConsumerFactory<String, CommentDto> consumerFactoryCommentEvent() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs(),
                new StringDeserializer(), new JsonDeserializer<>(CommentDto.class, false));
    }

    @Bean
    public ConsumerFactory<String, PostViewEvent> consumerFactoryPostViewEvent() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs(),
                new StringDeserializer(), new JsonDeserializer<>(PostViewEvent.class, false));
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, LikeDto>> listenerLikeEvent() {
        ConcurrentKafkaListenerContainerFactory<String, LikeDto> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactoryLikeEvent());
        return factory;
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, CommentDto>> listenerCommentEvent() {
        ConcurrentKafkaListenerContainerFactory<String, CommentDto> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactoryCommentEvent());
        return factory;
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, PostEvent>> listenerPostEvent() {
        ConcurrentKafkaListenerContainerFactory<String, PostEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactoryPostEvent());
        return factory;
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, PostViewEvent>> listenerPostViewEvent() {
        ConcurrentKafkaListenerContainerFactory<String, PostViewEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactoryPostViewEvent());
        return factory;
    }
}
