package faang.school.postservice.config.kafka.topics;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopics {
    @Value("${kafka.topics.partitions}")
    private int partitions;
    @Value("${kafka.topics.replications}")
    private short replications;
    @Value("${kafka.topics.like_event}")
    private String likeTopic;
    @Value("${kafka.topics.comment_event}")
    private String commentTopic;
    @Value("${kafka.topics.post_event}")
    private String postTopic;
    @Value("${kafka.topics.post_view_event}")
    private String postViewTopic;

    @Bean
    public NewTopic likeTopic() {
        return new NewTopic(likeTopic, partitions, replications);
    }

    @Bean
    public NewTopic commentTopic() {
        return new NewTopic(commentTopic, partitions, replications);
    }

    @Bean
    public NewTopic postTopic() {
        return new NewTopic(postTopic, partitions, replications);
    }

    @Bean
    public NewTopic postViewTopic() {
        return new NewTopic(postViewTopic, partitions, replications);
    }
}
