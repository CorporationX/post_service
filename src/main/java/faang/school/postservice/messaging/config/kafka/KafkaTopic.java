package faang.school.postservice.messaging.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * KafkaTopic — описание класса.
 * <p>
 * TODO: описать, какие обязанности у класса.
 * </p>
 *
 * @author Myrza
 * @since 05.08.2025
 */
@Configuration
public class KafkaTopic {
    @Value("${kafka.topics.create-post}")
    private String createTopicName;
    @Value("${kafka.topics.update-post}")
    private String updateTopicName;

    @Bean
    public NewTopic createTopic() {
        return TopicBuilder.name(createTopicName).build();
    }

    @Bean
    public NewTopic updateTopic() {
        return TopicBuilder.name(updateTopicName).build();
    }
}