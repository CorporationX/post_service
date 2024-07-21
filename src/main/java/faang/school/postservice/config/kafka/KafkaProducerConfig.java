package faang.school.postservice.config.kafka;

import faang.school.postservice.dto.event.EventDto;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
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
    private final KafkaProperties kafkaProperties;

    @Bean
    public ProducerFactory<String, EventDto> producerFactory() {
        Map<String, Object> producerProps = new HashMap<>();
        producerProps.put(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                kafkaProperties.getAddress()
        );
        producerProps.put(
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class
        );
        producerProps.put(
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                JsonSerializer.class
        );

        return new DefaultKafkaProducerFactory<>(producerProps);
    }

    @Bean
    public KafkaTemplate<String, EventDto> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public NewTopic postKafkaTopic() {
        return new NewTopic(
                kafkaProperties.getTopicsNames().getPost(),
                1,
                (short) 1
        );
    }

    @Bean
    public NewTopic likeKafkaTopic() {
        return new NewTopic(
                kafkaProperties.getTopicsNames().getLike(),
                1,
                (short) 1
        );
    }

    @Bean
    public NewTopic commentKafkaTopic() {
        return new NewTopic(
                kafkaProperties.getTopicsNames().getComment(),
                1,
                (short) 1
        );
    }
}
