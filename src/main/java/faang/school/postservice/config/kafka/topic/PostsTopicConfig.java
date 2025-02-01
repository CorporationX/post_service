package faang.school.postservice.config.kafka.topic;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PostsTopicConfig {
    @Value("${spring.data.kafka.topics.posts.name}")
    private String topicName;
    @Value("${spring.data.kafka.topics.posts.partitions}")
    private int partitions;
    @Value("${spring.data.kafka.topics.posts.replicas}")
    private short replicas;

    @Bean
    public NewTopic posts() {
        return new NewTopic(topicName, partitions, replicas);
    }
}
