package faang.school.postservice.config.kafka;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.like.LikeEventDto;
import faang.school.postservice.dto.post.PostEvent;
import faang.school.postservice.dto.post.PostViewEvent;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return props;
    }

    @Bean
    public ProducerFactory<String, PostEvent> producerFactoryPostEvent() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public ProducerFactory<String, PostViewEvent> producerFactoryPostViewEvent() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public KafkaTemplate<String, PostEvent> kafkaTemplateForPostEvent() {
        return new KafkaTemplate<>(producerFactoryPostEvent());
    }

    @Bean
    public KafkaTemplate<String, PostViewEvent> kafkaTemplateForPostViewEvent() {
        return new KafkaTemplate<>(producerFactoryPostViewEvent());
    }

    @Bean
    public ProducerFactory<String, LikeDto> producerFactoryLike() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public KafkaTemplate<String, LikeDto> kafkaTemplateForLike() {
        return new KafkaTemplate<>(producerFactoryLike());
    }

    @Bean
    public ProducerFactory<String, CommentDto> producerFactoryComment() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public KafkaTemplate<String, CommentDto> kafkaTemplateForComment() {
        return new KafkaTemplate<>(producerFactoryComment());
    }
}
