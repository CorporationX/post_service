package faang.school.postservice.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopic {
    @Value("${spring.kafka.topic.post.published}")
    private String postPublishedTopic;

    @Bean
    public NewTopic postPublishedTopic() {
        return TopicBuilder.name(postPublishedTopic).build();
    }
}
