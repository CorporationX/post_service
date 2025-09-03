package faang.school.postservice.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {
    @Value("${spring.kafka.topics.post-comment-publish.name}")
    private String postCommentPublishedTopic;

    @Value("${spring.kafka.topics.post-like-publish.name}")
    private String postLikePublishedTopic;

    @Bean
    public NewTopic postCommentPublishedTopic() {
        return TopicBuilder.name(postCommentPublishedTopic).build();
    }

    @Bean
    public NewTopic postLikePublishedTopic() {
        return TopicBuilder.name(postLikePublishedTopic).build();
    }
}
