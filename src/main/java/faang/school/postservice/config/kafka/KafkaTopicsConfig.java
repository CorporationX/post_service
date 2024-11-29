package faang.school.postservice.config.kafka;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class KafkaTopicsConfig {
    @Value("${spring.kafka.topic-name.post-comments}")
    private String postCommentsTopic;

    @Value("${spring.kafka.topic-name.posts}")
    private String postsTopic;

    @Value("${spring.kafka.topic-name.post-likes}")
    private String postLikesTopic;

    @Value("${spring.kafka.topic-name.post-views}")
    private String postViewsTopic;

    @Value("${spring.kafka.topic-name.heat-feed}")
    private String heatFeedTopic;
}
