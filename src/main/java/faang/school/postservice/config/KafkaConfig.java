package faang.school.postservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;


@Configuration
@EnableKafka
public class KafkaConfig {

    private static final String COMMENT_EVENTS_TOPIC = "notifications.events";

    @Bean
    public NewTopic commentEventsTopic() {
        return TopicBuilder.name(COMMENT_EVENTS_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
