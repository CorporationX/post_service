package faang.school.postservice.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    @Value("${spring.kafka.topic.posts.name}")
    private String topicPostName;
    @Value("${spring.kafka.topic.comments.name}")
    private String topicCommentName;
    @Value("${spring.kafka.topic.post-views.name}")
    private String topicPostViewsName;
    @Value("${spring.kafka.topic.like.name}")
    private String topicLikeName;
    @Value("${spring.kafka.topic.partitions}")
    private int partitions;
    @Value("${spring.kafka.topic.replication-factor}")
    private short replications;
    @Bean
    public NewTopic postsTopic() {
        return new NewTopic(topicPostName, partitions, replications);
    }

    @Bean
    public NewTopic commentsTopic() {
        return new NewTopic(topicCommentName, partitions, replications);
    }

    @Bean
    public NewTopic postViewsTopic(){
        return new NewTopic(topicPostViewsName, partitions, replications);
    }

    @Bean
    public NewTopic likeTopic(){
        return new NewTopic(topicLikeName, partitions,replications);
    }
}
