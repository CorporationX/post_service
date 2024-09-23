package faang.school.postservice.config.kafka.publisher.topics;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfigPostsView {

    @Value("${spring.kafka.topic.post_views}")
    private String postViewsTopicName;

    @Bean
    public NewTopic postViewsTopic(){
        return TopicBuilder.name(postViewsTopicName).build();
    }
}
