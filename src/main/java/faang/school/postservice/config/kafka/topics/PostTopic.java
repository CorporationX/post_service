package faang.school.postservice.config.kafka.topics;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PostTopic {
    @Value("${spring.data.kafka.topics.post_topic}")
    private String postTopic;

    @Bean
    public NewTopic postTopic() {
        return new NewTopic(postTopic, 1, (short) 1);
    }
}
