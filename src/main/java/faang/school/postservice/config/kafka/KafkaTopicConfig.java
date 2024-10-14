package faang.school.postservice.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Value("${spring.kafka.topic.likes.name}")
    private String likesTopicName;
    @Value("${spring.kafka.topic.likes.partitions}")
    private Integer likesPartitionsNumber;

    @Value("${spring.kafka.topic.posts.name}")
    private String postsTopicName;
    @Value("${spring.kafka.topic.posts.partitions}")
    private Integer postsPartitionsNumber;

    @Value("${spring.kafka.topic.posts_view.name}")
    private String postsViewTopicName;
    @Value("${spring.kafka.topic.posts_view.partitions}")
    private Integer postsViewPartitionsNumber;

    @Value("${spring.kafka.topic.comments.name}")
    private String commentTopicName;
    @Value("${spring.kafka.topic.posts_view.partitions}")
    private Integer commentsPartitionsNumber;

    @Bean
    public NewTopic likesTopic() {
        return TopicBuilder.name(likesTopicName)
                .partitions(likesPartitionsNumber)
                .build();
    }

    @Bean
    public NewTopic postsTopic() {
        return TopicBuilder.name(postsTopicName)
                .partitions(postsPartitionsNumber)
                .build();
    }

    @Bean
    public NewTopic postsViewTopic() {
        return TopicBuilder.name(postsViewTopicName)
                .partitions(postsViewPartitionsNumber)
                .build();
    }

    @Bean
    public NewTopic commentsTopic() {
        return TopicBuilder.name(commentTopicName)
                .partitions(commentsPartitionsNumber)
                .build();
    }
}
