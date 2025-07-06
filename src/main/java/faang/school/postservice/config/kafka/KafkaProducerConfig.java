package faang.school.postservice.config.kafka;

import com.fasterxml.jackson.databind.ser.std.StringSerializer;
import faang.school.postservice.dto.notification.CommentLikedEvent;
import faang.school.postservice.dto.notification.PostLikedEvent;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class KafkaProducerConfig {

    private final KafkaProperties properties;

    @Bean
    public KafkaTemplate<String, CommentLikedEvent> commentLikedEventTemplate() {
        return new KafkaTemplate<String, CommentLikedEvent>(producerFactory());
    }

    @Bean
    public KafkaTemplate<String, PostLikedEvent> postLikedEventTemplate() {
        return new KafkaTemplate<String, PostLikedEvent>(producerFactory());
    }

    private <T> ProducerFactory<String, T> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, properties.getBootstrapServers());
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
        return new DefaultKafkaProducerFactory<>(configProps);
    }
}
