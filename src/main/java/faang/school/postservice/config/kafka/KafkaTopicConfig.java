package faang.school.postservice.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {
    @Value("${spring.kafka.topics.post-comment-published.name}")
    private String postCommentPublishedTopicName;

    @Bean
    public NewTopic postCommentPublishedTopic() {
        return TopicBuilder.name(postCommentPublishedTopicName).build();
    }
}
