package faang.school.postservice.kafka;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class KafkaTopicProperties {
    @Value("${spring.kafka.topic.post.published}")
    private String postPublishedTopic;
    @Value("${spring.kafka.topic.post.viewed}")
    private String postViewedTopic;

    @Value("${spring.kafka.topic.like.added}")
    private String likeAddedTopic;

    @Value("${spring.kafka.topic.comment.added}")
    private String commentAddedTopic;

    @Value("${spring.kafka.topic.heater.users}")
    private String heaterUsersTopic;
    @Value("${spring.kafka.topic.heater.news-feeds}")
    private String heaterNewsFeedsTopic;
    @Value("${spring.kafka.topic.heater.posts}")
    private String heaterPostsTopic;
}
