package faang.school.postservice.config;

import faang.school.postservice.dto.kafka.CommentEventDto;
import faang.school.postservice.dto.post.PostViewEvent;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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

    private Map<String, Object> createBaseProducerConfig() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return props;
    }

    private <T> ProducerFactory<String, T> createProducerFactory(Class<T> valueType) {
        return new DefaultKafkaProducerFactory<>(createBaseProducerConfig());
    }

    private <T> KafkaTemplate<String, T> createKafkaTemplate(Class<T> valueType) {
        return new KafkaTemplate<>(createProducerFactory(valueType));
    }

    @Bean
    @Primary
    public ProducerFactory<String, Object> producerFactory() {
        return createProducerFactory(Object.class);
    }

    @Bean
    @Primary
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return createKafkaTemplate(Object.class);
    }

    @Bean("postViewEventProducerFactory")
    public ProducerFactory<String, PostViewEvent> postViewEventProducerFactory() {
        return createProducerFactory(PostViewEvent.class);
    }

    @Bean("postViewEventKafkaTemplate")
    public KafkaTemplate<String, PostViewEvent> postViewEventKafkaTemplate() {
        return createKafkaTemplate(PostViewEvent.class);
    }

    @Bean("commentEventProducerFactory")
    public ProducerFactory<String, CommentEventDto> commentEventProducerFactory() {
        return createProducerFactory(CommentEventDto.class);
    }

    @Bean("commentEventKafkaTemplate")
    public KafkaTemplate<String, CommentEventDto> commentEventKafkaTemplate() {
        return createKafkaTemplate(CommentEventDto.class);
    }
}