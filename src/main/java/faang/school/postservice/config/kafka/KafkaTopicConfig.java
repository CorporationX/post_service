package faang.school.postservice.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    @Value("${spring.data.kafka.posts-topic}")
    private String postsTopic;

    @Bean
    public NewTopic commentEventTopic() {
        return new NewTopic(postsTopic, 1, (short) 1);
    }
}
