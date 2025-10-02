package faang.school.postservice.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;

/**
 * KafkaConfig — конфигурация для создания топиков в Kafka
 *
 * @author bozya
 * @since 26.09.2025
 */
@Configuration
@EnableKafka
public class KafkaConfig {

    @Bean
    public NewTopic postsTopic(@Value("${spring.kafka.topics.posts}") String topic) {
        return TopicBuilder.name(topic)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic feedsTopic(@Value("${spring.kafka.topics.feeds}") String topic) {
        return TopicBuilder.name(topic)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic commentsTopic(@Value("${spring.kafka.topics.comments}") String topic) {
        return TopicBuilder.name(topic)
                .partitions(1)
                .replicas(1)
                .build();
    }
}