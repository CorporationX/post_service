package faang.school.postservice.config;

import faang.school.postservice.dto.kafka.CommentAnalysisEventDto;
import faang.school.postservice.dto.kafka.CommentEventDto;
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

    private Map<String, Object> createBaseProducerConfig() {
        Map<String, Object> props = new HashMap<>();

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return props;
    }

    @Bean("stringCommentAnalysisEventDtoProducerFactory")
    public ProducerFactory<String, CommentAnalysisEventDto> stringCommentAnalysisEventDtoProducerFactory() {
        return createProducerFactory(CommentAnalysisEventDto.class);
    }

    @Bean("stringCommentAnalysisEventDtoKafkaTemplate")
    public KafkaTemplate<String, CommentAnalysisEventDto> stringCommentAnalysisEventDtoKafkaTemplate() {
        return createKafkaTemplate(CommentAnalysisEventDto.class);
    }

    @Bean("stringCommentEventDtoProducerFactory")
    public ProducerFactory<String, CommentEventDto> stringCommentEventDtoProducerFactory() {
        return createProducerFactory(CommentEventDto.class);
    }

    @Bean("stringCommentEventDtoKafkaTemplate")
    public KafkaTemplate<String, CommentEventDto> stringCommentEventDtoKafkaTemplate() {
        return createKafkaTemplate(CommentEventDto.class);
    }

    private <T> ProducerFactory<String, T> createProducerFactory(Class<T> valueType) {
        return new DefaultKafkaProducerFactory<>(createBaseProducerConfig());
    }

    private <T> KafkaTemplate<String, T> createKafkaTemplate(Class<T> valueType) {
        return new KafkaTemplate<>(createProducerFactory(valueType));
    }
}