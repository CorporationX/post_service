package faang.school.postservice.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    @Value("${kafka.topic.post-viewed-event.name}")
    private String postViewedEventTopic;
    @Value("${kafka.topic.post-viewed-event.partition}")
    private int partition;
    @Value("${kafka.topic.post-viewed-event.replicationFactor}")
    private short replicationFactor;

    @Bean
    public NewTopic postViewedEventTopic() {
        return new NewTopic(postViewedEventTopic, partition, replicationFactor);
    }
}
