package faang.school.postservice.config.kafka;

import faang.school.postservice.dto.event.PostViewEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
public class KafkaProducerConfig {

    @Bean
    public KafkaTemplate<String, PostViewEvent> kafkaTemplate(
            ProducerFactory<String, PostViewEvent> producerFactory
    ) {
        return new KafkaTemplate<>(producerFactory);
    }
}