package faang.school.postservice.messaging.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Конфигурация Kafka-топиков, используемых сервисом.
 * <p>
 * Определяет бины {@link NewTopic} для автоматического создания топиков Kafka
 * при запуске приложения (если включена настройка auto-create в брокере или
 * используется AdminClient).
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
    @Value("${kafka.topics.delete-post}")
    private String deleteTopicName;

    @Value("${kafka.topics.publish-post}")
    private String publishTopicName;

    @Bean
    public NewTopic createTopic() {
        return TopicBuilder.name(createTopicName).build();
    }

    @Bean
    public NewTopic updateTopic() {
        return TopicBuilder.name(updateTopicName).build();
    }

    @Bean
    public NewTopic deleteTopic() {
        return TopicBuilder.name(deleteTopicName).build();
    }

    @Bean
    public NewTopic publishTopic() {
        return TopicBuilder.name(publishTopicName).build();
    }
}