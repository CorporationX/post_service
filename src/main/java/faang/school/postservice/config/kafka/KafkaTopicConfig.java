package faang.school.postservice.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    @Value("${spring.data.kafka.posts-topic}")
    private String postsTopic;
    @Value("${spring.data.kafka.comments-topic}")
    private String commentTopic;
    @Value("${spring.data.kafka.likes-topic}")
    private String likesTopic;

    @Bean
    public NewTopic postEventTopic() {
        return new NewTopic(postsTopic, 1, (short) 1);
    }

    @Bean
    public NewTopic commentEventTopic() {
        return new NewTopic(commentTopic, 1, (short) 1);
    }

    @Bean
    public NewTopic likesEventTopic() {
        return new NewTopic(likesTopic, 1, (short) 1);
    }
}
