package faang.school.postservice.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * Конфигурационный класс для создания топика Kafka
 *
 * @author Linempy
 * @since 22.09.2025
 */
@Configuration
@EnableKafka
public class KafkaConfig {

    @Value("${spring.kafka.topics.posts}")
    private String postsTopic;

    @Value("${spring.kafka.topics.feeds}")
    private String feedsTopic;

    @Bean
    public NewTopic topicPosts() {
        return new NewTopic(postsTopic, 1, (short) 1);
    }

    @Bean
    public NewTopic topicFeed() {
        return new NewTopic(feedsTopic, 1, (short) 1);
    }
}