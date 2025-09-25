package faang.school.postservice.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурационный класс для создания топика Kafka
 *
 * @author Linempy
 * @since 22.09.2025
 */
@Configuration
public class KafkaConfig {

    @Value("${kafka.topics.posts}")
    private String topicPosts;

    @Value("${kafka.topics.feeds}")
    private String topicFeeds;

    @Bean
    public NewTopic topicPosts() {
        return new NewTopic(topicPosts, 1, (short) 1);
    }

    @Bean
    public NewTopic topicFeed() {
        return new NewTopic(topicFeeds, 1, (short) 1);
    }
}