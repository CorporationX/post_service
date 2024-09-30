package faang.school.postservice.config.kafka.publisher.topics;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfigLikes {

    @Value("${spring.kafka.topic.likes}")
    private String likesTopicName;

    @Bean
    public NewTopic likesTopic(){
        return TopicBuilder.name(likesTopicName).build();
    }
}
