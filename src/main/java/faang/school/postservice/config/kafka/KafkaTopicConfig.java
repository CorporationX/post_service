package faang.school.postservice.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    @Value("${spring.kafka.topic.comment-topic}")
    private String commentTopic;

    @Bean
    public NewTopic commentTopic() {
        return new NewTopic(commentTopic, 3, (short) 1);
    }
}
