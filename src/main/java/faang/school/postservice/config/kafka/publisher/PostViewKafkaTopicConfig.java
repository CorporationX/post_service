package faang.school.postservice.config.kafka.publisher;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class PostViewKafkaTopicConfig {
    @Value("${spring.kafka.topic.post_views}")
    private String postViewsTopicName;

    @Bean
    public NewTopic postViewsKafkaTopic(){
        return TopicBuilder.name(postViewsTopicName).build();
    }
}