package faang.school.postservice.config.kafka;

import org.springframework.beans.factory.annotation.Value;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Value("${spring.kafka.topics.post-comment-publish.name}")
    private String postCommentPublishedTopic;
    @Value("${spring.kafka.topics.post-like-publish.name}")
    private String postLikePublishedTopic;
    @Value("${spring.kafka.topics.post-view.name}")
    private String postViewTopic;
    @Value("${spring.kafka.topics.post.name}")
    private String postTopic;

    @Bean
    public NewTopic postCommentPublishedTopic() {
        return TopicBuilder.name(postCommentPublishedTopic).build();
    }

    @Bean
    public NewTopic postLikePublishedTopic() {
        return TopicBuilder.name(postLikePublishedTopic).build();
    }

    @Bean
    public NewTopic postViewTopic() {
        return TopicBuilder.name(postViewTopic).build();
    }

    @Bean
    public NewTopic postCreateTopic() {
        return TopicBuilder.name(postTopic).build();
    }
}
