package faang.school.postservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
public class KafkaConfig {
    @Value("${spring.kafka.topics.posts}")
    private String postsTopic;

    @Bean
    public KafkaTemplate<Long, Object> kafkaTemplate(ProducerFactory<Long, Object> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public NewTopic commentsTopic() {
        return new NewTopic(postsTopic, 3, (short) 1);
    }
}
