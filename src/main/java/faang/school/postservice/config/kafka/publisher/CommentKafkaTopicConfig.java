package faang.school.postservice.config.kafka.publisher;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class CommentKafkaTopicConfig {
    @Value("${spring.kafka.topic.comments}")
    private String commentTopicName;

    @Bean
    public NewTopic commentKafkaTopic(){
        return TopicBuilder.name(commentTopicName).build();
    }
}
