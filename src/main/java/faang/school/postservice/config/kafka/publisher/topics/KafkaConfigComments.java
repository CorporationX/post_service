package faang.school.postservice.config.kafka.publisher.topics;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfigComments {

    @Value("${spring.kafka.topic.comments}")
    private String commentsTopicName;

    @Bean
    public NewTopic commentsTopic(){
        return TopicBuilder.name(commentsTopicName).build();
    }
}
